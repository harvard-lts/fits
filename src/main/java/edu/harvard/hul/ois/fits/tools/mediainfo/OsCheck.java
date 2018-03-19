//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools.mediainfo;

/**
 * helper class to check the operating system this Java VM runs in
 *
 * please keep the notes below as a pseudo-license
 *
 * http://stackoverflow.com/questions/228477/how-do-i-programmatically-determine-operating-system-in-java
 * compare to http://svn.terracotta.org/svn/tc/dso/tags/2.6.4/code/base/common/src/com/tc/util/runtime/Os.java
 * http://www.docjar.com/html/api/org/apache/commons/lang/SystemUtils.java.html
 */
import java.util.Locale;

public final class OsCheck {

  /**
   * types of Operating Systems
   */
  public enum OSType {
    Windows, MacOS, Linux, Other
  };

  // cached result of OS detection
  protected static OSType detectedOS;

  /**
   * detect the operating system from the os.name System property and cache
   * the result
   *
   * @return - the operating system detected
   */
  public static OSType getOperatingSystemType() {
    if (detectedOS == null) {
      String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
      if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
        detectedOS = OSType.MacOS;
      } else if (OS.indexOf("win") >= 0) {
        detectedOS = OSType.Windows;
      } else if (OS.indexOf("linux") >= 0) {
        detectedOS = OSType.Linux;
      } else {
        detectedOS = OSType.Other;
      }
    }
    return detectedOS;
  }
}
