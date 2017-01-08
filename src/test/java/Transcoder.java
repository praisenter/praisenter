
import java.io.IOException;
import java.nio.file.Path;

import io.humble.video.AudioFormat;
import io.humble.video.Codec;
import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerStream;
import io.humble.video.Encoder;
import io.humble.video.MediaAudio;
import io.humble.video.MediaAudioResampler;
import io.humble.video.MediaDescriptor;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.MediaResampler;
import io.humble.video.MediaSampled;
import io.humble.video.Muxer;
import io.humble.video.MuxerFormat;
import io.humble.video.MuxerStream;

public final class Transcoder {
	private static class DemuxerStreamHelper {
		MediaDescriptor.Type type;
		DemuxerStream stream;
		Decoder decoder;
		MediaSampled media;
	}

	private static class MuxerStreamHelper {
		MediaDescriptor.Type type;
		MuxerStream stream;
		MediaResampler resampler;
		MediaSampled media;
		Encoder encoder;
	}

	public static void transcode(Path from) throws InterruptedException, IOException {
		Demuxer source = Demuxer.make();
		source.open(from.toAbsolutePath().toString(), null, false, true, null, null);
		int n = source.getNumStreams();

		DemuxerStreamHelper[] inputHelpers = new DemuxerStreamHelper[n];
		for (int i = 0; i < n; i++) {
			DemuxerStream stream = source.getStream(i);
			Decoder decoder = stream.getDecoder();
			if (decoder == null) {
				break;
			}

			DemuxerStreamHelper helper = new DemuxerStreamHelper();
			helper.stream = stream;
			helper.decoder = decoder;

			decoder.open(null, null);
			helper.type = decoder.getCodecType();
			if (helper.type == MediaDescriptor.Type.MEDIA_AUDIO) {
				helper.media = MediaAudio.make(decoder.getFrameSize(),
						decoder.getSampleRate(), decoder.getChannels(),
						decoder.getChannelLayout(), decoder.getSampleFormat());
			} else if (helper.type == MediaDescriptor.Type.MEDIA_VIDEO) {
				helper.media = MediaPicture.make(decoder.getWidth(),
						decoder.getHeight(), decoder.getPixelFormat());
			}

			System.out.println(String.format(
					"Codec: %1$s\n"
					+ "Channel Layout: %2$s\n"
					+ "Channels: %3$d\n"
					+ "Frame Size: %4$d\n"
					+ "Number of Properties: %5$d\n"
					+ "Pixel Format: %6$s\n"
					+ "Sample Format: %7$s\n"
					+ "Sample Rate: %8$d\n"
					+ "Time Base: %9$s\n"
					+ "Width: %10$d\n"
					+ "Height: %11$d\n\n", 
					
					decoder.getCodec().getLongName(), 
					decoder.getChannelLayout().toString(),
					decoder.getChannels(),
					decoder.getFrameSize(),
					decoder.getNumProperties(),
					decoder.getPixelFormat().toString(),
					decoder.getSampleFormat().toString(),
					decoder.getSampleRate(),
					decoder.getTimeBase().toString(),
					decoder.getWidth(),
					decoder.getHeight()));
			
			inputHelpers[i] = helper;
		}

		Path to = from.getParent().resolve(from.getFileName().toString() + ".mp4");
		Muxer muxer = Muxer.make(to.toAbsolutePath().toString(), null, null);
		MuxerFormat format = muxer.getFormat();

		MuxerStreamHelper[] outputHelpers = new MuxerStreamHelper[n];
		for (int i = 0; i < n; i++) {
			DemuxerStreamHelper input = inputHelpers[i];
			if (input == null) {
				break;
			}

			MuxerStreamHelper output = new MuxerStreamHelper();
			output.type = input.type;
			Encoder encoder = null;
			if (output.type == MediaDescriptor.Type.MEDIA_VIDEO) {
				Codec codec = Codec.findEncodingCodec(Codec.ID.CODEC_ID_H264);
				encoder = Encoder.make(codec);

				encoder.setWidth(input.decoder.getWidth());
				encoder.setHeight(input.decoder.getHeight());
				encoder.setPixelFormat(input.decoder.getPixelFormat());
//				encoder.setProperty("b", 400000l); // bitrate
//				encoder.setProperty("g", 10l); // gop
//				encoder.setProperty("bf", 0l); // max b frames
//				Rational tb = Rational.make(1, 2997);
//				encoder.setTimeBase(tb);
			} else if (output.type == MediaDescriptor.Type.MEDIA_AUDIO) {
				Codec codec = Codec.findEncodingCodec(Codec.ID.CODEC_ID_AAC);
				encoder = Encoder.make(codec);

				encoder.setSampleRate(input.decoder.getSampleRate());
				encoder.setSampleFormat(AudioFormat.Type.SAMPLE_FMT_S16);
				encoder.setChannelLayout(input.decoder.getChannelLayout());
				encoder.setChannels(input.decoder.getChannels());
//				encoder.setProperty("b", 64000l); // bitrate
//				Rational tb = Rational.make(1, input.decoder.getSampleRate());
//				encoder.setTimeBase(tb);
			}
			
			if (encoder == null) continue;
			
			output.encoder = encoder;
			if (output.encoder != null) {
				if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER)) {
					output.encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
				}

				output.encoder.open(null, null);
				output.stream = muxer.addNewStream(output.encoder);
			}

			output.media = input.media;
			output.resampler = null;
			if (output.type == MediaDescriptor.Type.MEDIA_AUDIO) {
				if (output.encoder.getSampleRate() != input.decoder.getSampleRate() || 
					output.encoder.getSampleFormat() != input.decoder.getSampleFormat() || 
					output.encoder.getChannelLayout() != input.decoder.getChannelLayout() || 
					output.encoder.getChannels() != input.decoder.getChannels()) {
					
					MediaAudioResampler resampler = MediaAudioResampler.make(
							output.encoder.getChannelLayout(),
							output.encoder.getSampleRate(),
							output.encoder.getSampleFormat(),
							input.decoder.getChannelLayout(),
							input.decoder.getSampleRate(),
							input.decoder.getSampleFormat());

					resampler.open();
					output.media = MediaAudio.make(
							output.encoder.getFrameSize(),
							output.encoder.getSampleRate(),
							output.encoder.getChannels(),
							output.encoder.getChannelLayout(),
							output.encoder.getSampleFormat());
				}
			}
			
			outputHelpers[i] = output;
		}

		muxer.open(null, null);

		MediaPacket packet = MediaPacket.make();
		while (source.read(packet) >= 0) {
			if (packet.isComplete()) {
				int streamNo = packet.getStreamIndex();
				DemuxerStreamHelper input = inputHelpers[streamNo];
				MuxerStreamHelper output = outputHelpers[streamNo];
				if (input != null && output != null) {
					decodeAndEncode(packet, input.decoder, input.media,
							output.resampler, output.media, muxer,
							output.encoder);
				}
			}
		}

		for (int i = 0; i < n; i++) {
			DemuxerStreamHelper input = inputHelpers[i];
			MuxerStreamHelper output = outputHelpers[i];
			if (input != null && output != null) {
				decodeAndEncode(null, input.decoder, input.media,
						output.resampler, output.media, muxer, output.encoder);
			}

		}
		source.close();
		muxer.close();
	}

	private static final void decodeAndEncode(MediaPacket packet,
			Decoder decoder, MediaSampled input, MediaResampler resampler,
			MediaSampled output, Muxer muxer, Encoder encoder) {
		int offset = 0;
		int bytesRead = 0;
		do {
			bytesRead += decoder.decode(input, packet, offset);
			if (input.isComplete()) {
				resampleEncodeAndMux(input, resampler, output, muxer, encoder);
			}
			offset += bytesRead;
		} while ((packet == null && input.isComplete())
				|| (packet != null && offset < packet.getSize()));
		if (packet == null) {
			encodeAndMux(null, muxer, encoder);
		}
	}

	private static final void resampleEncodeAndMux(MediaSampled input,
			MediaResampler resampler, MediaSampled output, Muxer muxer,
			Encoder encoder) {
		do {
			if (resampler != null) {
				resampler.resample(output, input);
			}
			
			// i added this line - it would fail saying that the media was incomplete
			// without it
			if (!output.isComplete()) continue;
			
			encodeAndMux(output, muxer, encoder);
		} while (input == null && output.isComplete());
	}

	private static final void encodeAndMux(MediaSampled media, Muxer muxer,
			Encoder encoder) {
		MediaPacket packet = MediaPacket.make();
		do {
			encoder.encode(packet, media);
			if (packet.isComplete()) {
				muxer.write(packet, true);
			}
		} while (media == null && packet.isComplete());
	}
}
