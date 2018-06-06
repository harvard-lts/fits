/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *  2016 - Minor changes have been made to the original code by Harvard University.
 *  The original Apache license still applies.
 */

package nz.govt.natlib.adapter.wordperfect;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import nz.govt.natlib.adapter.AdapterUtils;
import nz.govt.natlib.adapter.DataAdapter;
import nz.govt.natlib.fx.BooleanElement;
import nz.govt.natlib.fx.ByteChomperElement;
import nz.govt.natlib.fx.CompoundBitElement;
import nz.govt.natlib.fx.CompoundElement;
import nz.govt.natlib.fx.DataSource;
import nz.govt.natlib.fx.Element;
import nz.govt.natlib.fx.EnumeratedElement;
import nz.govt.natlib.fx.FXUtil;
import nz.govt.natlib.fx.FileDataSource;
import nz.govt.natlib.fx.FixedLengthStringElement;
import nz.govt.natlib.fx.IntegerElement;
import nz.govt.natlib.fx.ParserContext;
import nz.govt.natlib.fx.StringElement;
import nz.govt.natlib.fx.WPMajorVersionIntegerElement;
import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

/**
 * Adapter for WordPerfect documents.
 * 
 * @author unascribed
 * @version 1.0
 */

public class WPAdapter extends DataAdapter {

	// header
	private Element wpHeaderElement = new CompoundElement(
			new String[] { "marker", "file-type", "offset", "product",
					"file-sub-type", "major-version", "minor-version",
					"encrypted", "reserved" },
			new Element[] {
					new ByteChomperElement(IntegerElement.BYTE_SIZE),
					// new
					// IntegerElement(IntegerElement.BYTE_SIZE,false,IntegerElement.DECIMAL_FORMAT),
					new FixedLengthStringElement(3, true),
					new ByteChomperElement(IntegerElement.INT_SIZE),
					// new
					// IntegerElement(IntegerElement.INT_SIZE,false,IntegerElement.DECIMAL_FORMAT),
					new EnumeratedElement(new IntegerElement(
							IntegerElement.BYTE_SIZE, false,
							IntegerElement.HEX_FORMAT), new String[] { "0x1",
							"0x2", "0x3", "0x4", "0x5", "0x6", "0x7", "0x8",
							"0x9", "0xa", "0xb", "0xc", "0xd", "0xe", "0xf",
							"0x10" }, new String[] { "WordPerfect", "Shell",
							"Notebook", "Calculator", "File Manager",
							"Calendar", "Program Editor/Ed Editor",
							"Macro Editor", "PlanPerfect", "DataPerfect",
							"Mail", "Printer (PTR.EXE)", "Scheduler",
							"WordPerfect Office", "DrawPerfect",
							"LetterPerfect" }, "unknown"),
					new EnumeratedElement(
							new IntegerElement(IntegerElement.BYTE_SIZE, false,
									IntegerElement.HEX_FORMAT),
							new String[] { "0x1", "0x2", "0x3", "0xa", "0xb",
									"0xc", "0xd", "0xe", "0xf", "0x10", "0x11",
									"0x12", "0x13", "0x14", "0x15", "0x16",
									"0x17", "0x18", "0x19", "0x1a", "0x1b",
									"0x1c", "0x1d", "0x1e", "0x1f", "0x20",
									"0x21", "0x22", "0x23", "0x24", "0x25",
									"0x26", "0x27", "0x28", "0x29", "0x2a",
									"0x2b", "0x2c", "0x2d", "0x2e", "0x2f",
									"0x30", "0x38" },
							new String[] {
									"macro file",
									"WordPerfect help file",
									"keyboard definition file",
									"Document",
									"dictionary file",
									"thesaurus file",
									"block",
									"rectangular block",
									"column block",
									"printer resource file (PRS)",
									"setup file",
									"prefix information file",
									"printer resource file (ALL)",
									"display resource file (DRS)",
									"overlay file (WP.FIL)",
									"graphics file (WPG)",
									"hyphenation code module",
									"hyphenation data module",
									"macro resource file (MRS)",
									"graphics driver (WPD)",
									"hyphenation lex module",
									"Printer Q codes (used by VAX/DG)",
									"Spell code module�word list",
									"WP.QRS file (WP5.1 equation resource file)",
									"Reserved", "VAX .SET",
									"Spell code module�rules",
									"Dictionary�rules", "Reserved",
									"WP5.1 Graphics/Text Drivers",
									"Rhymer word file (WPCorp product, TSR)",
									"Rhymer pronunciation file", "Reserved",
									"Reserved",
									"WP51.INS file (install options file)",
									"Mouse driver for WP5.1",
									"UNIX Setup file for WP5.0",
									"MAC WP2.0 document",
									"VAX file (WP4.2 document)",
									"External Spell Code Module (WP5.1)",
									"External Spell Dictionary",
									"MAC SOFT graphics file",
									"WPWin 5.1 Application Resource Library added for WPWin 5.1" },
							"unknown"),
					new WPMajorVersionIntegerElement(IntegerElement.BYTE_SIZE,
							false),
					new IntegerElement(IntegerElement.BYTE_SIZE, false,
							IntegerElement.DECIMAL_FORMAT),
					new BooleanElement(IntegerElement.SHORT_SIZE),
					new ByteChomperElement(2) });

	// graphics
	private Element graphicElement = new CompoundElement(
			new String[] { "graphic-images" },
			new Element[] { new IntegerElement(IntegerElement.SHORT_SIZE,
					false, IntegerElement.DECIMAL_FORMAT),
			// :
			// : note: there's more but only the image data itself...
			});

	// printer
	private Element printerElement = new CompoundElement(new String[] { "name",
			"filename", "n/o", "top-margin", "bottom-margin", "left-margin",
			"right-margin", "flags", "date", "time" }, new Element[] {
			new FixedLengthStringElement(37, true),
			new FixedLengthStringElement(13, true),
			new ByteChomperElement(24),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new CompoundBitElement(new String[] { "selected", "initialise",
					"hzone-diabled", "orientation" },
					new CompoundBitElement.BitElement[] {
							new CompoundBitElement.BooleanBitReader(1),
							new CompoundBitElement.BooleanBitReader(1),
							new CompoundBitElement.BooleanBitReader(1),
							new CompoundBitElement.BitReader(4), }, false),
			new CompoundBitElement(new String[] { "day", "month", "year" },
					new CompoundBitElement.BitElement[] {
							new CompoundBitElement.BitReader(4),
							new CompoundBitElement.BitReader(4),
							new CompoundBitElement.AddingBitReader(7, 80), },
					false),
			new CompoundBitElement(new String[] { "seconds", "minutes" },
					new CompoundBitElement.BitElement[] {
							new CompoundBitElement.BitReader(4),
							new CompoundBitElement.BitReader(4), }, false), });

	// document flags
	private Element documentElement = new CompoundElement(new String[] {
			"flags", "quality", "redline-char", "width", "binding-width",
			"printer-select", "reserved", "display-pitch", "resolution",
			"reserved" }, new Element[] {
			new CompoundBitElement(new String[] { "display-pitch-auto", "",
					"requires-formatting", "requires-regenerating",
					"manual-pitch", "requires-update-links",
					"do-not-display-codes" },
					new CompoundBitElement.BitElement[] {
							new CompoundBitElement.BooleanBitReader(1),
							new CompoundBitElement.BitChomper(2),
							new CompoundBitElement.BooleanBitReader(1),
							new CompoundBitElement.BooleanBitReader(1),
							new CompoundBitElement.BooleanBitReader(1),
							new CompoundBitElement.BooleanBitReader(1),
							new CompoundBitElement.BooleanBitReader(1), },
					false),
			new CompoundBitElement(new String[] { "print-quality",
					"graphics-quality" }, new CompoundBitElement.BitElement[] {
					new CompoundBitElement.BitReader(4),
					new CompoundBitElement.BitReader(4), }, false),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.BYTE_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new ByteChomperElement(IntegerElement.SHORT_SIZE),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new IntegerElement(IntegerElement.SHORT_SIZE, false,
					IntegerElement.DECIMAL_FORMAT),
			new ByteChomperElement(IntegerElement.SHORT_SIZE), });

	// document summary part1 (5.0)
	private Element summaryElement = new CompoundElement(new String[] {
			"created", "name", "type", "subject", "author", "typist",
			"abstract" }, new Element[] { new StringElement(),
			new StringElement(), new StringElement(), new StringElement(),
			new StringElement(), new StringElement(), new StringElement(), });

	// document summary part2 (5.1+)
	private Element summary2Element = new CompoundElement(new String[] {
			"account", "keywords", "", "created-ISO", "" }, new Element[] {
			new StringElement(), new StringElement(),
			new ByteChomperElement(1), new StringElement(),
			new ByteChomperElement(1), });

	public String getVersion() {
		return "1.0";
	}

	public boolean acceptsFile(File file) {
		boolean wp = false;
		DataSource ftk = null;
		try {
			ftk = new FileDataSource(file);
			// Header and default information
			byte[] fileHead = ftk.getData(4);
			// decimal values of expected first 4 bytes of a Word Perfect file
			byte[] acceptableWordPerfectHead = { -1, 87, 80, 67 };  // {(unprintable),W,P,C} for "�WPC"
			// compare inital bytes of input file with expected bytes
			wp = Arrays.equals(fileHead, acceptableWordPerfectHead);
			
//			String head = FXUtil.getFixedStringValue(ftk, 4);
//			if ((head.equals("�WPC"))) { // DO NOT USE: different encoding on different platforms
//				wp = true;
//			}
			
		} catch (IOException ex) {
			LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,
					"IO Exception determining WordPerfect file type");
		}
		finally {
			AdapterUtils.close(ftk);
		}
		return wp;
	}

	public String getOutputType() {
		return "wordperfect.dtd";
	}

	public String getInputType() {
		return "application/vnd.wordperfect";
	}

	public String getName() {
		return "Corel Wordperfect Adapter";
	}

	public String getDescription() {
		return "Adapts all Corel WordPerfect files from version 5.1 to 12.0";
	}

	public void adapt(File file, ParserContext ctx) throws IOException {
		// Add the MetaData to the tree!
		DataSource ftk = new FileDataSource(file);
		ctx.fireStartParseEvent("wordperfect");
		writeFileInfo(file, ctx);
		// ctx.fireParseEvent("Version", "");
		try {
			ctx.fireStartParseEvent("header");
			wpHeaderElement.read(ftk, ctx);
			ctx.fireEndParseEvent("header");
			boolean fivePointOnePlus = ctx
					.getIntAttribute("wordperfect.header.minor-version") >= 1;
			boolean sixPlus = Double.parseDouble(ctx
					.getAttribute("wordperfect.header.major-version")
					+ "") >= 6;

			if (sixPlus) {
				// next version should encompass WordPerfect 6.0+
			} else {

				IndexHeaderElement indexReader = new IndexHeaderElement();
				PacketHeaderElement blockHead = new PacketHeaderElement();
				boolean readingBlocks = true;
				boolean summary = false;
				while (readingBlocks) {
					indexReader.read(ftk, ctx);
					// System.out.println(indexReader);
					for (int i = 0; i < indexReader.count - 1; i++) {
						blockHead.read(ftk, ctx);
						// arrange to retrive the data from the appropriate
						// place
						long pos = ftk.getPosition();
						ftk.setPosition(blockHead.dataOffset);

						// System.out.println(" >"+blockHead);
						switch (blockHead.getType()) {
						case 0xc:
							ctx.fireStartParseEvent("printer");
							printerElement.read(ftk, ctx);
							ctx.fireEndParseEvent("printer");
							break;
						case 0x8:
							ctx.fireStartParseEvent("graphics");
							graphicElement.read(ftk, ctx);
							ctx.fireEndParseEvent("graphics");
							break;
						case 0x6:
							ctx.fireStartParseEvent("document");
							documentElement.read(ftk, ctx);
							ctx.fireEndParseEvent("document");
							break;
						case 0x1:
							ctx.fireStartParseEvent("summary");
							summary = true;
							// System.out.println("Found Sumary at :"
							//		+ ftk.getPosition());
							summaryElement.read(ftk, ctx);
							if (fivePointOnePlus) {
								summary2Element.read(ftk, ctx);
							}
							ctx.fireEndParseEvent("summary");
							break;
						}

						// move back to the next block...
						ftk.setPosition(pos);
					}

					// now move to the next block...
					if (indexReader.getNextBlockOffset() == 0) {
						readingBlocks = false;
						break;
					}
					ftk.setPosition(indexReader.getNextBlockOffset());
				}

				if (!summary) {
					ctx.fireParseEvent("summary", "");
				}
			}
		} finally {
			AdapterUtils.close(ftk);
		}
		ctx.fireEndParseEvent("wordperfect");
	}

	private class IndexHeaderElement extends Element {
		int type = 0;

		int count = 0;

		int length = 0;

		int nextBlockOffset = 0;

		public void read(DataSource data, ParserContext ctx) throws IOException {
			byte[] buf = data.getData(IntegerElement.SHORT_SIZE);
			type = (int) FXUtil.getNumericalValue(buf, false);
			buf = data.getData(IntegerElement.SHORT_SIZE);
			count = (int) FXUtil.getNumericalValue(buf, false);
			buf = data.getData(IntegerElement.SHORT_SIZE);
			length = (int) FXUtil.getNumericalValue(buf, false);
			buf = data.getData(IntegerElement.INT_SIZE);
			nextBlockOffset = (int) FXUtil.getNumericalValue(buf, false);
		}

		public String toString() {
			return "WP Index [" + type + "]: indices=" + count + ", len="
					+ length + ", next=" + nextBlockOffset;
		}

		/**
		 * @return
		 */
		public int getCount() {
			return count;
		}

		/**
		 * @return
		 */
		public int getLength() {
			return length;
		}

		/**
		 * @return
		 */
		public int getNextBlockOffset() {
			return nextBlockOffset;
		}

		/**
		 * @return
		 */
		public int getType() {
			return type;
		}

	}

	private class PacketHeaderElement extends Element {
		int type = 0;

		int length = 0;

		int dataOffset = 0;

		public void read(DataSource data, ParserContext ctx) throws IOException {
			byte[] buf = data.getData(IntegerElement.SHORT_SIZE);
			type = (int) FXUtil.getNumericalValue(buf, false);
			buf = data.getData(IntegerElement.INT_SIZE);
			length = (int) FXUtil.getNumericalValue(buf, false);
			buf = data.getData(IntegerElement.INT_SIZE);
			dataOffset = (int) FXUtil.getNumericalValue(buf, false);
		}

		public String toString() {
			return "WP Block [" + type + "]: len=" + length + ", offset="
					+ dataOffset;
		}

		/**
		 * @return
		 */
		public int getDataOffset() {
			return dataOffset;
		}

		/**
		 * @return
		 */
		public int getLength() {
			return length;
		}

		/**
		 * @return
		 */
		public int getType() {
			return type;
		}

	}

}
