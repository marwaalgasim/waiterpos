package ua.cn.yet.waiter.ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import ua.cn.yet.waiter.util.Utils;

/* ImageFilter.java is used by FileChooserDemo2.java. */
public class ImageFilter extends FileFilter {

    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = Utils.getExtension(f);
        if (extension != null) {
        	for (int i = 0; i < Utils.imageExt.length; i++) {
				if (extension.compareToIgnoreCase(Utils.imageExt[i]) == 0) {
					return true;
				}
			}
            return false;
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
    	StringBuilder sb = new StringBuilder("Картинки (");
    	for (int i = 0; i < Utils.imageExt.length; i++) {
    		sb.append(Utils.imageExt[i]);
    		if (i != (Utils.imageExt.length-1)) {
    			sb.append(", ");
    		}
    	}
    	sb.append(")");
        return sb.toString();
    }
}