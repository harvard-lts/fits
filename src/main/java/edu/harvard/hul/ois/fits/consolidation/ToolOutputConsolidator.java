//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//


package edu.harvard.hul.ois.fits.consolidation;

import java.util.List;

import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.tools.ToolOutput;

public interface ToolOutputConsolidator {

	public FitsOutput processResults(List<ToolOutput> results);

}
