
    ______ ______                                  __            _  __     __
   / ____// ____/____ ___   ____   ___   ____ _   / /_   __  __ (_)/ /____/ /
  / /_   / /_   / __ `__ \ / __ \ / _ \ / __ `/  / __ \ / / / // // // __  / 
 / __/  / __/  / / / / / // /_/ //  __// /_/ /  / /_/ // /_/ // // // /_/ /  
/_/    /_/    /_/ /_/ /_// .___/ \___/ \__, /  /_.___/ \__,_//_//_/ \__,_/   
                        /_/           /____/                                 


                build: ffmpeg-3.3.3-32bit-static.tar.xz
              version: 3.3.3
 
                  gcc: 6.4.0
                 yasm: 1.3.0.28.g51af
                 nasm: 2.13.01

               libass: 0.13.7
               libvpx: 1.6.1-1000-gfacb12494
              libx264: 0.152.19 ba24899
              libx265: 2.5+6-d11482e5fedb
              libxvid: 1.3.4-1+b2
              libwebp: 0.5.2 
              libzimg: 2.5.1
              librtmp: 2.4
            libgnutls: 3.3.28
            libtheora: 1.2.0alpha1+svn
            libfrei0r: 1.6.1-1
           libvidstab: 1.10
          libfreetype: 2.8-0.2
          libopenjpeg: 1.5.2 

              libsoxr: 0.1.3b1
              libopus: 1.2.1
             libspeex: 1.2
            libvorbis: 1.3.5
           libmp3lame: 3.99.5
       libvo-amrwbenc: 0.1.3-1
    libopencore-amrnb: 0.1.3-2.1+b2
    libopencore-amrwb: 0.1.3-2.1+b2

      For HEVC/H.265 encoding:  ffmpeg -h encoder=libx265
                                http://x265.readthedocs.org/en/default/cli.html#standalone-executable-options

      For AVC/H.264 encoding:   ffmpeg -h encoder=libx264
                                http://mewiki.project357.com/wiki/X264_Settings

                 FFmpeg Wiki:   https://trac.ffmpeg.org/wiki


      Notes: ffmpeg-10bit has support for AVC/H.264, HEVC/H.265, and VP9 high bit depth encoding.
             However, the 32bit builds lack HEVC/H.265 high bit depth because it's not supported by
             libx265.

             A limitation of statically linking glibc is the loss of DNS resolution. Installing
             nscd through your package manager will fix this or you can use
             "ffmpeg -i http://<ip address here>/" instead of "ffmpeg -i http://example.com/"

      This static build is licensed under the GNU General Public License version 3.

      If you appreciate this up-to-date build of FFmpeg and my time that goes into to maintaining it,
      please consider making a donation. Thank you.

      
      Paypal tinyurl: http://goo.gl/1Ol8N
      Bitcoin: 18aREiQcTTem7uJLSYSdyu6bk8tMncZruk

      email: john.vansickle@gmail.com
      irc: relaxed @ irc://irc.freenode.net #ffmpeg
      url: http://johnvansickle.com/ffmpeg/
