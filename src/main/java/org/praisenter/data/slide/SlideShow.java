package org.praisenter.data.slide;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.praisenter.Constants;
import org.praisenter.Editable;
import org.praisenter.Watchable;
import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.Persistable;
import org.praisenter.data.Tag;
import org.praisenter.data.json.InstantJsonDeserializer;
import org.praisenter.data.json.InstantJsonSerializer;
import org.praisenter.data.search.Indexable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY)
@JsonTypeName(value = "slideshow")
@Editable
public final class SlideShow implements ReadOnlySlideShow, Indexable, Persistable, Copyable, Identifiable {

	public static final String DATA_TYPE_SLIDE_SHOW = "show";
	
	private final StringProperty format;
	private final StringProperty version;
	private final ObjectProperty<UUID> id;
	private final StringProperty name;
	private final ObjectProperty<Instant> createdDate;
	private final ObjectProperty<Instant> modifiedDate;
	private final BooleanProperty loopEnabled;
	
	private final ObservableList<SlideAssignment> slides;
	private final ObservableList<SlideAssignment> slidesReadOnly;
	private final ObservableSet<Tag> tags;
	private final ObservableSet<Tag> tagsReadOnly;
	
	public SlideShow() {
		this.format = new SimpleStringProperty(Constants.FORMAT_NAME);
		this.version = new SimpleStringProperty(Constants.VERSION);
		this.id = new SimpleObjectProperty<UUID>(UUID.randomUUID());
		this.name = new SimpleStringProperty();
		this.createdDate = new SimpleObjectProperty<>(Instant.now());
		this.modifiedDate = new SimpleObjectProperty<>(this.createdDate.get());
		this.loopEnabled = new SimpleBooleanProperty(false);
		this.slides = FXCollections.observableArrayList();
		this.slidesReadOnly = FXCollections.unmodifiableObservableList(this.slides);
		this.tags = FXCollections.observableSet(new HashSet<>());
		this.tagsReadOnly = FXCollections.unmodifiableObservableSet(this.tags);
	}

	@Override
	public SlideShow copy() {
		SlideShow show = new SlideShow();
		show.createdDate.set(this.createdDate.get());
		show.format.set(this.format.get());
		show.id.set(this.id.get());
		show.loopEnabled.set(this.loopEnabled.get());
		show.modifiedDate.set(this.modifiedDate.get());
		show.name.set(this.name.get());
		show.version.set(this.version.get());
		for (SlideAssignment sa : this.slides) {
			show.slides.add(sa.copy());
		}
		show.tags.addAll(this.tags);
		return show;
	}

	@Override
	public String toString() {
		return this.name.get();
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.identityEquals(obj);
	}
	
	@Override
	public int hashCode() {
		return this.id.get().hashCode();
	}
	
	@Override
	public boolean identityEquals(Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (other instanceof SlideShow) {
			return this.id.get().equals(((SlideShow)other).id.get());
		}
		return false;
	}
	
	@Override
	public List<Document> index() {
		List<Document> documents = new ArrayList<Document>();
		
		Document document = new Document();

		// allow filtering by the bible id
		document.add(new StringField(FIELD_ID, this.getId().toString(), Field.Store.YES));
		
		// allow filtering by type
		document.add(new StringField(FIELD_TYPE, DATA_TYPE_SLIDE_SHOW, Field.Store.YES));
		
		StringBuilder sb = new StringBuilder();
		sb.append(this.name.get());
		
		for (Tag tag : this.tags) {
			sb.append(" ").append(tag.getName());
		}
		
		document.add(new TextField(FIELD_TEXT, sb.toString(), Field.Store.YES));
		
		documents.add(document);
		
		return documents;
	}
	
	@Override
	@JsonProperty(Constants.FORMAT_PROPERTY_NAME)
	public String getFormat() {
		return this.format.get();
	}
	
	@JsonProperty(Constants.FORMAT_PROPERTY_NAME)
	private void setFormat(String format) {
		this.format.set(format);
	}
	
	@Override
	public StringProperty formatProperty() {
		return this.format;
	}
	
	@Override
	@JsonProperty(Constants.VERSION_PROPERTY_NAME)
	public String getVersion() {
		return this.version.get();
	}
	
	@JsonProperty(Constants.VERSION_PROPERTY_NAME)
	public void setVersion(String version) {
		this.version.set(version);
	}
	
	@Override
	public StringProperty versionProperty() {
		return this.version;
	}
	
	@Override
	@JsonProperty
	public UUID getId() {
		return this.id.get();
	}
	
	@JsonProperty
	public void setId(UUID id) {
		this.id.set(id);
	}
	
	@Override
	public ObjectProperty<UUID> idProperty() {
		return this.id;
	}
	
	@Override
	@JsonProperty
	public String getName() {
		return this.name.get();
	}
	
	@JsonProperty
	public void setName(String name) {
		this.name.set(name);
	}
	
	@Override
	@Watchable(name = "name")
	public StringProperty nameProperty() {
		return this.name;
	}

	@Override
	@JsonProperty
	@JsonSerialize(using = InstantJsonSerializer.class)
	public Instant getCreatedDate() {
		return this.createdDate.get();
	}
	
	@JsonProperty
	@JsonDeserialize(using = InstantJsonDeserializer.class)
	public void setCreatedDate(Instant date) {
		this.createdDate.set(date);
	}
	
	@Override
	public ObjectProperty<Instant> createdDateProperty() {
		return this.createdDate;
	}

	@Override
	@JsonProperty
	@JsonSerialize(using = InstantJsonSerializer.class)
	public Instant getModifiedDate() {
		return this.modifiedDate.get();
	}

	@JsonProperty
	@JsonDeserialize(using = InstantJsonDeserializer.class)
	public void setModifiedDate(Instant date) {
		this.modifiedDate.set(date);
	}
	
	@Override
	public ObjectProperty<Instant> modifiedDateProperty() {
		return this.modifiedDate;
	}
	
	@Override
	@JsonProperty
	public boolean isLoopEnabled() {
		return this.loopEnabled.get();
	}
	
	@JsonProperty
	public void setLoopEnabled(boolean enabled) {
		this.loopEnabled.set(enabled);
	}
	
	@Override
	@Watchable(name = "loopEnabled")
	public BooleanProperty loopEnabledProperty() {
		return this.loopEnabled;
	}
	
	@Override
	public ObservableList<SlideAssignment> getSlidesUnmodifiable() {
		return this.slidesReadOnly;
	}
	
	@JsonProperty
	@Watchable(name = "slides")
	public ObservableList<SlideAssignment> getSlides() {
		return this.slides;
	}
	
	@JsonProperty
	public void setSlides(List<SlideAssignment> slides) {
		this.slides.setAll(slides);
	}
	
	@JsonProperty
	@Watchable(name = "tags")
	public ObservableSet<Tag> getTags() {
		return this.tags;
	}
	
	@JsonProperty
	public void setTags(Set<Tag> tags) {
		this.tags.addAll(tags);
	}
	
	@Override
	public ObservableSet<Tag> getTagsUnmodifiable() {
		return this.tagsReadOnly;
	}
}
