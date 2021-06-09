package org.praisenter.ui.display;

import java.util.List;

import org.praisenter.data.configuration.Display;
import org.praisenter.data.configuration.DisplayRole;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.MappedList;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

public final class DisplaysController extends BorderPane {
	private final GlobalContext context;
	
	private final MappedList<Tab, DisplayTarget> displayTargetToTabMapping;
	
	public DisplaysController(GlobalContext context) {
		this.context = context;
		
		this.displayTargetToTabMapping = new MappedList<Tab, DisplayTarget>(context.getDisplayManager().getDisplayTargets(), (DisplayTarget d) -> {
			Tab tab = new Tab();
			tab.setText(d.toString());
			tab.setClosable(false);
			tab.setContent(new DisplayController(context, d));
			return tab;
		});
		
		TabPane tabs = new TabPane();
		Bindings.bindContent(tabs.getTabs(), this.displayTargetToTabMapping);
		
		tabs.getSelectionModel().select(this.getDefaultSelectedTabIndex(context.getConfiguration().getDisplays()));
		
		this.setCenter(tabs);
	}
	
	private int getDefaultSelectedTabIndex(List<? extends Display> displays) {
		int index = 0;
		for (Display d : this.context.getConfiguration().getDisplays()) {
			if (d.getRole() == DisplayRole.MAIN) {
				index = d.getId();
				break;
			}
		}
		return index;
	}
	
}
