//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits;

import edu.harvard.hul.ois.ots.schemas.TextMD.CharacterInfo;
import edu.harvard.hul.ois.ots.schemas.TextMD.MarkupBasis;
import edu.harvard.hul.ois.ots.schemas.TextMD.MarkupLanguage;
import edu.harvard.hul.ois.ots.schemas.TextMD.TextMD;

public class TextMDModel {

    protected TextMD textMD;
    protected MarkupLanguage ml;
    protected MarkupBasis mb;
    protected CharacterInfo ci;

    protected TextMDModel () {
        textMD = new TextMD ();
        ml = null;
    }

    protected void attachMarkupLanguage () {
        if (ml == null) {
            ml = new MarkupLanguage ();
            textMD.addMarkupLanguage(ml);
        }
    }

    protected void attachMarkupBasis () {
        if (mb == null) {
            mb = new MarkupBasis ();
            textMD.addMarkupBasis(mb);
        }
    }

    protected void attachCharacterInfo () {
        if (ci == null) {
            ci = new CharacterInfo ();
            textMD.addCharacterInfo(ci);
        }
    }
}
