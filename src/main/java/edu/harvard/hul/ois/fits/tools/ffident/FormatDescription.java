//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools.ffident;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Data class to store information on a file format.
 * @author Marco Schmidt
 */
public class FormatDescription implements Comparable<FormatDescription> {
    private List<String> fileExtensions;
    private String group;
    private String longName;
    private byte[] magicBytes;
    private List<String> mimeTypes;
    private Integer minimumSize;
    private Integer offset;
    private String shortName;

    /**
     * Add a single file extension to the internal list of typical file extensions for this format.
     * @param ext file extension
     */
    public void addFileExtension(String ext) {
        if (ext == null || ext.length() < 1) {
            return;
        }
        if (fileExtensions == null) {
            fileExtensions = new ArrayList<>();
        }
        fileExtensions.add(ext);
    }

    /**
     * Add all file extensions to the internal list of typical file extensions for this format.
     * @param ext a comma-separated list of typical file extensions
     */
    public void addFileExtensions(String ext) {
        if (ext == null) {
            return;
        }
        if (ext.indexOf(',') == -1) {
            addFileExtension(ext);
            return;
        }
        String[] extensions = ext.split(",");
        for (int i = 0; i < extensions.length; i++) {
            addFileExtension(extensions[i]);
        }
    }

    /**
     * Add a single MIME type to the internal list of MIME types for this format.
     * @param mimeType the MIME type to be added
     */
    public void addMimeType(String mimeType) {
        if (mimeType == null || mimeType.length() < 1) {
            return;
        }
        if (mimeTypes == null) {
            mimeTypes = new ArrayList<>();
        }
        mimeTypes.add(mimeType);
    }

    /**
     * Add a list of MIME types to the internal list of MIME types for this format.
     * @param mimeType a comma-separated list of MIME types to be added
     */
    public void addMimeTypes(String mimeType) {
        if (mimeType == null) {
            return;
        }
        if (mimeType.indexOf(',') == -1) {
            addMimeType(mimeType);
            return;
        }
        String[] types = mimeType.split(",");
        for (int i = 0; i < types.length; i++) {
            addMimeType(types[i]);
        }
    }

    public int compareTo(FormatDescription desc) {
        // FormatDescription desc = (FormatDescription)obj;
        int relation = getGroup().compareTo(desc.getGroup());
        if (relation != 0) {
            return relation;
        }
        return getLongName().compareTo(desc.getLongName());
    }

    public List<String> getFileExtensions() {
        return fileExtensions;
    }

    public String getGroup() {
        return group;
    }

    public String getLongName() {
        return longName;
    }

    public byte[] getMagicBytes() {
        return magicBytes;
    }

    public String getMimeType() {
        return getMimeType(0);
    }

    public String getMimeType(int index) {
        if (mimeTypes != null && index >= 0 && index < mimeTypes.size()) {
            return mimeTypes.get(index);
        } else {
            return null;
        }
    }

    public List<String> getMimeTypes() {
        return mimeTypes;
    }

    public Integer getMinimumSize() {
        return minimumSize;
    }

    public Integer getOffset() {
        return offset;
    }

    public String getShortName() {
        return shortName;
    }

    public boolean matches(byte[] data) {
        if (magicBytes == null || offset == null) {
            return false;
        }
        int index1 = 0;
        int index2 = offset;
        if (index2 + magicBytes.length > data.length) {
            return false;
        }
        int num = magicBytes.length;
        do {
            if (magicBytes[index1++] != data[index2++]) {
                return false;
            }
            num--;
        } while (num > 0);
        return true;
    }

    public void setGroup(String newValue) {
        group = newValue;
    }

    public void setLongName(String newValue) {
        longName = newValue;
    }

    public void setMagicBytes(byte[] newValue) {
        magicBytes = newValue;
    }

    public void setMagicBytes(String newValue) {
        if (newValue == null || newValue.length() < 1) {
            magicBytes = null;
            return;
        }
        if (newValue.length() > 2 && newValue.charAt(0) == '"' && newValue.charAt(newValue.length() - 1) == '"') {
            newValue = newValue.substring(1, newValue.length() - 1);
            magicBytes = newValue.getBytes(StandardCharsets.ISO_8859_1);
            return;
        }
        if ((newValue.length() % 2) == 0) {
            newValue = newValue.toLowerCase();
            byte[] data = new byte[newValue.length() / 2];
            int byteValue = 0;
            for (int i = 0; i < newValue.length(); i++) {
                char c = newValue.charAt(i);
                int number;
                if (c >= '0' && c <= '9') {
                    number = c - '0';
                } else if (c >= 'a' && c <= 'f') {
                    number = 10 + c - 'a';
                } else {
                    return;
                }
                if ((i % 2) == 0) {
                    byteValue = number * 16;
                } else {
                    byteValue += number;
                    data[i / 2] = (byte) byteValue;
                }
            }
            magicBytes = data;
        }
    }

    public void setMinimumSize(Integer newValue) {
        minimumSize = newValue;
    }

    public void setOffset(Integer newValue) {
        offset = newValue;
    }

    public void setShortName(String newValue) {
        shortName = newValue;
    }

    public String toString() {
        final String PRIMARY_SEPARATOR = "\t";
        final String SECONDARY_SEPARATOR = ",";
        StringBuilder sb = new StringBuilder(80);
        sb.append(getGroup());
        sb.append(PRIMARY_SEPARATOR);
        sb.append(getShortName());
        sb.append(PRIMARY_SEPARATOR);
        sb.append(getLongName());
        sb.append(PRIMARY_SEPARATOR);
        if (getMimeTypes() != null) {
            Iterator<String> iter = getMimeTypes().iterator();
            while (iter.hasNext()) {
                sb.append(iter.next());
                sb.append(SECONDARY_SEPARATOR);
            }
        } else {
            sb.append(PRIMARY_SEPARATOR);
        }
        sb.append(PRIMARY_SEPARATOR);
        if (getFileExtensions() != null) {
            Iterator<String> iter = getFileExtensions().iterator();
            while (iter.hasNext()) {
                sb.append(iter.next());
                sb.append(SECONDARY_SEPARATOR);
            }
        } else {
            sb.append(PRIMARY_SEPARATOR);
        }
        sb.append(getOffset());
        sb.append(PRIMARY_SEPARATOR);
        sb.append(PRIMARY_SEPARATOR);
        return sb.toString();
    }
}
