package edu.harvard.hul.ois.fits.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.identity.ExternalIdentifier;
import edu.harvard.hul.ois.fits.identity.FitsIdentity;

public class Report {
	
	
	public void run(String fitsDirectory, String corporaDirectory, String outputFile, String groundTruth) throws IOException, FitsException{
		
		Fits fits = new Fits(fitsDirectory); 
		
		
		Collection<File> files =  FileUtils.listFiles(new File(corporaDirectory), TrueFileFilter.TRUE, FileFilterUtils.makeSVNAware(null));
		
		Map<String, ExtensionStat> results = new TreeMap<String,ExtensionStat>();
		Map<String, List<FileStat>> fileStats = new TreeMap<String,List<FileStat>>();
		for(File f : files){
			System.out.println("Processing "+f.getAbsolutePath());
			String extension = "N/A";
			if(f.getName().contains(".")){
				extension = f.getName().substring(f.getName().lastIndexOf(".")+1).toLowerCase();
			}
			FitsOutput fitsOut = fits.examine(f);
			
			
			
			
			/*
			Document d = fitsOut.getFitsXml();
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		    String xmlString = outputter.outputString(d);
		    System.out.println(xmlString);
			*/
			
			
			
			
			
			
			String mimeType = null;
			String puid = null;
			String format=null;
			String valid=null;
			
			
			if(fitsOut.checkValid()!=null){
				if(fitsOut.checkValid()){
					valid="true";
				}else{
					valid="false";
				}
			}else{
				valid="";
			}
			if(fitsOut.getIdentities()!=null && fitsOut.getIdentities().size()>0){
				for(FitsIdentity fi : fitsOut.getIdentities()){
					
					if(mimeType==null && fi.getMimetype()!=null && fi.getMimetype().trim().length()>0){
						mimeType = fi.getMimetype();
					}
					if(format==null && fi.getFormat()!=null && fi.getFormat().length()>0){
						format = fi.getFormat();
					}
					System.out.println("MIMETYPE:"+fi.getMimetype());
					System.out.println("FORMAT:"+fi.getFormat());
					for(ExternalIdentifier ei : fi.getExternalIdentifiers()){
						if(ei.getName().toLowerCase().contains("puid")){
							System.out.println("PUID:"+ei.getValue());
							if(puid==null && ei.getValue()!=null && ei.getValue().length()>0){
								puid = ei.getValue();
							}
						}
					}
				}
			}
			
			
			if(results.keySet().contains(extension)){
				ExtensionStat es = results.get(extension);
				es.setTotal(es.getTotal()+1);
				if(puid!=null){
					es.setWithPUID(es.getWithPUID()+1);
				}
				if(mimeType!=null){
					es.setWithMimeType(es.getWithMimeType()+1);
				}
				if(valid!=null){
					if(valid.equals("true")){
						es.setValid(es.getValid()+1);
					}else if(valid.equals("false")){
						es.setNotValid(es.getNotValid()+1);
					}else{
						es.setUnknownValid(es.getUnknownValid()+1);
					}
				}
				results.put(extension, es);
			}else{
				ExtensionStat es = new ExtensionStat();
				es.setTotal(1);
				es.setExtension(extension);
				if(puid!=null){
					es.setWithPUID(1);
				}
				if(mimeType!=null){
					es.setWithMimeType(1);
				}
				if(valid!=null){
					if(valid.equals("true")){
						es.setValid(1);
						es.setNotValid(0);
						es.setUnknownValid(0);
					}else if(valid.equals("false")){
						es.setValid(0);
						es.setNotValid(1);
						es.setUnknownValid(0);
					}else{
						es.setValid(0);
						es.setNotValid(0);
						es.setUnknownValid(1);
					}
				}
				results.put(extension, es);
			}
			
			
			FileStat fs = new FileStat();
			fs.setName(f.getAbsolutePath());
			fs.setMimetype(mimeType);
			fs.setPuid(puid);
			fs.setValid(valid);
			fs.setFormat(format);
			
			if(fileStats.keySet().contains(extension)){
				List<FileStat> temp = fileStats.get(extension);
				temp.add(fs);
				fileStats.put(extension, temp);
			}else{
				List<FileStat> temp = new ArrayList<FileStat>();
				temp.add(fs);
				fileStats.put(extension, temp);
			}
			
			
		}
		
		
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("Resume");
		
		int rownum = 0;
		Row row = sheet.createRow(rownum++);
		Object[] headers = {"Extension","Total","With mimetype","With puid","Valid","Not valid","Unknown valid"};
		int cellnum = 0;
		
		for (Object obj : headers){
			Cell cell = row.createCell(cellnum++);
			if(obj instanceof String)
                cell.setCellValue((String)obj);
            else if(obj instanceof Integer)
                cell.setCellValue((Integer)obj);
        }
		for(Map.Entry<String, ExtensionStat> entry : results.entrySet()){
			row = sheet.createRow(rownum++);
			//Object[] data = {entry.getKey(),entry.getValue().getTotal(),entry.getValue().getWithMimeType(),entry.getValue().getWithPUID(),entry.getValue().getValid(),entry.getValue().getNotValid(),entry.getValue().getUnknownValid()};
			double withMimeTypePercent = ((double)entry.getValue().getWithMimeType()/entry.getValue().getTotal())*100.0;
			double withPUIDPercent = ((double)entry.getValue().getWithPUID()/entry.getValue().getTotal())*100.0;
			double validPercent = ((double)entry.getValue().getValid()/entry.getValue().getTotal())*100.0;
			double notValidPercent = ((double)entry.getValue().getNotValid()/entry.getValue().getTotal())*100.0;
			double unknownValidPercent = ((double)entry.getValue().getUnknownValid()/entry.getValue().getTotal())*100.0;
			//Object[] data = {entry.getKey(),entry.getValue().getTotal(),(float)entry.getValue().getWithMimeType(),entry.getValue().getWithPUID(),entry.getValue().getValid(),entry.getValue().getNotValid(),entry.getValue().getUnknownValid()};
			Object[] data = {entry.getKey(),entry.getValue().getTotal(),withMimeTypePercent,withPUIDPercent,validPercent,notValidPercent,unknownValidPercent};
			cellnum = 0;
            for (Object obj : data){
            	Cell cell = row.createCell(cellnum++);
                if(obj instanceof String)
                     cell.setCellValue((String)obj);
                 else if(obj instanceof Integer)
                     cell.setCellValue((Integer)obj);
                 else if(obj instanceof Double)
                     cell.setCellValue((Double)obj);
            }
		}
		
		/*
		for(Map.Entry<String, ExtensionStat> entry : results.entrySet()){
			row = sheet.createRow(rownum++);
			double withMimeTypePercent = ((double)entry.getValue().getWithMimeType()/entry.getValue().getTotal())*100.0;
			double withPUIDPercent = ((double)entry.getValue().getWithPUID()/entry.getValue().getTotal())*100.0;
			double validPercent = ((double)entry.getValue().getValid()/entry.getValue().getTotal())*100.0;
			double notValidPercent = ((double)entry.getValue().getNotValid()/entry.getValue().getTotal())*100.0;
			double unknownValidPercent = ((double)entry.getValue().getUnknownValid()/entry.getValue().getTotal())*100.0;
			//Object[] data = {entry.getKey(),entry.getValue().getTotal(),(float)entry.getValue().getWithMimeType(),entry.getValue().getWithPUID(),entry.getValue().getValid(),entry.getValue().getNotValid(),entry.getValue().getUnknownValid()};
			Object[] data = {entry.getValue().getTotal(),withMimeTypePercent,withPUIDPercent,validPercent,notValidPercent,unknownValidPercent};

			cellnum = 0;
            for (Object obj : data){
            	Cell cell = row.createCell(cellnum++);
                if(obj instanceof String)
                     cell.setCellValue((String)obj);
                 else if(obj instanceof Float)
                     cell.setCellValue((Float)obj);
                 else if(obj instanceof Integer)
                     cell.setCellValue((Integer)obj);
                 else if(obj instanceof Double)
                     cell.setCellValue((Double)obj);
            }
		}
		*/
		
		
		for(Map.Entry<String, List<FileStat>> entry : fileStats.entrySet()){
			Sheet sheetExtension = wb.createSheet(entry.getKey());
			rownum = 0;
			row = sheetExtension.createRow(rownum++);
			Object[] headersFile = {"Name","Mimetype","Format","PUID","Valid"};
			cellnum = 0;
			
			for (Object obj : headersFile){
				Cell cell = row.createCell(cellnum++);
				if(obj instanceof String)
	                cell.setCellValue((String)obj);
	            else if(obj instanceof Integer)
	                cell.setCellValue((Integer)obj);
	        }
			
			
			
			CellStyle redStyle = wb.createCellStyle();
			redStyle.setFillForegroundColor(HSSFColor.RED.index);  
			redStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
	        
	        CellStyle greenStyle = wb.createCellStyle();
	        greenStyle.setFillForegroundColor(HSSFColor.GREEN.index);  
	        greenStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
			
	        CellStyle yellowStyle = wb.createCellStyle();
	        yellowStyle.setFillForegroundColor(HSSFColor.YELLOW.index);  
	        yellowStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  

			
	        
	        if(groundTruth!=null){
				for(FileStat fs : entry.getValue()){
					row = sheetExtension.createRow(rownum++);
					Object[] data = {fs.getName(),fs.getMimetype(),fs.getFormat(),fs.getPuid(),fs.getValid()};
					String[] gt = getGroundTruth(groundTruth, fs.getName());
					cellnum = 0;
		            for (int i=0;i<data.length;i++){
		            	Object obj = data[i];
		            	
		            	Cell cell = row.createCell(cellnum++);
		                if(obj instanceof String){
		                	if(((String)obj)==null || ((String)obj).trim().equals("")){
		                		if(gt[i]==null || gt[i].trim().equals("")){
		                			
		                		}else{
		                			cell.setCellStyle(yellowStyle);
		                		}
		                	}else if(gt[i]==null || gt[i].trim().equals("")){
		                		
		                	}else if(((String)obj).equalsIgnoreCase(gt[i])){
		                		cell.setCellStyle(greenStyle);
		                	}else{
		                		cell.setCellStyle(redStyle);
		                	}
		                    cell.setCellValue((String)obj);
		                }
		                
		            }
				}
	        }
			
		}



		
		
		FileOutputStream fileOut = new FileOutputStream(outputFile);
		wb.write(fileOut);
		fileOut.close();
		
/*
 * 
		
		
		Map<String, Object[]> data = new TreeMap<String, Object[]>();
        data.put("1", new Object[] {"ID", "NAME", "LASTNAME"});
        data.put("2", new Object[] {1, "Amit", "Shukla"});
        data.put("3", new Object[] {2, "Lokesh", "Gupta"});
        data.put("4", new Object[] {3, "John", "Adwards"});
        data.put("5", new Object[] {4, "Brian", "Schultz"});
          
        //Iterate over data and write to sheet
        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset)
        {
            Row row = sheet.createRow(rownum++);
            Object [] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr)
            {
               Cell cell = row.createCell(cellnum++);
               if(obj instanceof String)
                    cell.setCellValue((String)obj);
                else if(obj instanceof Integer)
                    cell.setCellValue((Integer)obj);
            }
        }

		
		*/
	}
	
	
	private String[] getGroundTruth(String groundFile, String fileName){
		String[] t = new String[5];
		try{
			File excel = new File(groundFile);
			FileInputStream fis = new FileInputStream(excel);
		    
			Workbook wb = new HSSFWorkbook(fis);
			Sheet ws = wb.getSheetAt(0);
			int rowNum = ws.getLastRowNum()+1;
			for (int i=0; i<rowNum; i++){
				Row row = ws.getRow(i);
				if(row != null && row.getCell(0)!=null){
					if(row.getCell(0).getStringCellValue().equalsIgnoreCase(fileName)){
						t[0] = row.getCell(0)!=null?row.getCell(0).getStringCellValue():"";
						t[1] = row.getCell(1)!=null?row.getCell(1).getStringCellValue():"";
						t[2] = row.getCell(2)!=null?row.getCell(2).getStringCellValue():"";
						t[3] = row.getCell(3)!=null?row.getCell(3).getStringCellValue():"";
						t[4] =row.getCell(4)!=null?row.getCell(4).getStringCellValue():"";
						break;
					}
				}
			}
		
		}catch(Exception e){
			
			e.printStackTrace();
		}
		return t;
	}
	private void printHelp(Options opts) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("report", opts );
	}
	
	public static void main(String[] args) {
		
		try{
			
			Report r = new Report();
			Options options = new Options();
			options.addOption("d",true, "directory with files to process");
			options.addOption("f",false,"fits home");
			options.addOption("o",true, "output file");
			options.addOption("g",true, "ground truth file");
			options.addOption("h",false,"print this message");



			CommandLineParser parser = new GnuParser();
			CommandLine cmd = parser.parse(options, args);
			
			if(cmd.hasOption("h")) {
				r.printHelp(options);
				System.exit(0);
			}

			if(!cmd.hasOption("d") || !cmd.hasOption("f") || !cmd.hasOption("o")){
				r.printHelp(options);
				System.exit(0);
			}
			r.run(cmd.getOptionValue("f"), cmd.getOptionValue("d"), cmd.getOptionValue("o"),cmd.getOptionValue("g"));
			
			/*
			String fitsDirectory = "/home/sleroux/Development/fitsKEEP";
			//String corporaDirectory = "/home/sleroux/Development/RODA-GORDIC/02-support/02-legacy_data/test_corpora/Test Files";
			String corporaDirectory = "/home/sleroux/Development/Corpora/Ground Truth";
			String outputFile = "/home/sleroux/workbook.xls";
			//String groundTruth = "/home/sleroux/Development/RODA-GORDIC/02-support/02-legacy_data/test_corpora/Test_Files.xls";
			String groundTruth = "/home/sleroux/Development/Corpora/GroundTruth.xls";
			ReportMaker rm = new ReportMaker();
			rm.run(fitsDirectory,corporaDirectory,outputFile,groundTruth);
			*/
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
