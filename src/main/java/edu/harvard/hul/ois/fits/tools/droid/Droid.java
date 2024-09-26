//
// Copyright (c) 2016 by The President and Fellows of Harvard College
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the License at:
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permission and limitations under the License.
//

package edu.harvard.hul.ois.fits.tools.droid;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.fits.tools.ToolBase;
import edu.harvard.hul.ois.fits.tools.ToolInfo;
import edu.harvard.hul.ois.fits.tools.ToolOutput;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.nationalarchives.droid.command.action.VersionCommand;

/**  The principal glue class for invoking DROID under FITS.
 */
public class Droid extends ToolBase {

    private boolean enabled = true;
    private final Fits fits;

    private final DroidWrapper droidWrapper;

    private static final Logger logger = LoggerFactory.getLogger(Droid.class);

    public Droid(Fits fits) throws FitsToolException {
        super();
        this.fits = fits;
        logger.debug("Initializing Droid");
        info = new ToolInfo("Droid", getDroidVersion(), null);

        try {
            XMLConfiguration config = fits.getConfig();
            droidWrapper = DroidWrapperFactory.getOrCreateFactory(DroidConfig.fromFitsConfig(config))
                    .createInstance();
        } catch (Throwable e) {
            throw new FitsToolException("Error initializing DROID", e);
        }
    }

    @Override
    public ToolOutput extractInfo(File file) throws FitsToolException {
        logger.debug("Droid.extractInfo starting on {}", file.getName());
        Instant startTime = Instant.now();

        DroidResult result;

        try {
            result = droidWrapper.analyze(file.toPath());
        } catch (Exception e) {
            throw new FitsToolException("DROID can't query file " + file.getAbsolutePath(), e);
        }

        DroidToolOutputter outputter = new DroidToolOutputter(this, fits, result);
        ToolOutput output = outputter.toToolOutput();

        duration = Duration.between(startTime, Instant.now()).toMillis();
        runStatus = RunStatus.SUCCESSFUL;
        logger.debug("Droid.extractInfo finished on {}", file.getName());
        return output;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        enabled = value;
    }

    private String getDroidVersion() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        VersionCommand vcmd = new VersionCommand(pw);
        try {
            vcmd.execute();
        } catch (Exception e) {
            return "(Version unknown)";
        }
        return sw.toString().trim();
    }
}
