package com.nbcuni.tve.common;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;
import org.testng.Reporter;

import io.appium.java_client.AppiumDriver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppLib {

    private Config config;
    private AppiumDriver driver;
    private String pathToScreenshots;
    private String pathToTempLogs;
    private String pathToCharlesLogs;
    private String pathToCharlesFailureLogs;
    private String attachmentDateFormat;
    private String mobileOS;
    
    public AppLib(AppiumDriver driver) {
        this.driver = driver;
        config = new Config();
        pathToScreenshots = config.getFilePath("PathToScreenshots");
        pathToTempLogs = config.getFilePath("PathToTempLogs");
        pathToCharlesLogs = config.getFilePath("PathToCharlesLogs");
        pathToCharlesFailureLogs = config.getFilePath("PathToCharlesFailureLogs");
        attachmentDateFormat = "MMddyyhhmmssSSSa";
        mobileOS = config.getString("MobileOS");
    }

    public void attachScreenshot(ITestResult result) throws Exception {
        
        //set the screenshot file name based on date in ms and the test method name
        Date date = new Date(result.getEndMillis());
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(attachmentDateFormat);
        String screenshotDateTime = dateTimeFormat.format(date);
        String methodName = result.getMethod().getMethodName() + "_" + screenshotDateTime;
        
        //take the screenshot and name the file
        String filePath = pathToScreenshots + methodName + ".png";
        Reporter.setCurrentTestResult(result); 
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);    
        FileUtils.copyFile(scrFile, new File(filePath));
        System.out.println("Screenshot saved to " + filePath);
        
        //attach the screenshot to the reporter
        Reporter.log("<br><br><a href='" + filePath + "'> <img src='./" + methodName 
            + ".png' height='667' width='375'/> </a>");
        
    }
    
    public void attachAppXMLTree(final ITestResult result) throws Exception {

        // set the file name based on date in ms and the test method name
        Date date = new Date(result.getEndMillis());
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(attachmentDateFormat);
        String fileDateTime = dateTimeFormat.format(date);
        String methodName = result.getMethod().getMethodName() + "_XMLTreeLog_" + fileDateTime;

        // the screenshot directory should always exist but if not create it
        File logDir = new File(pathToScreenshots);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        // create the log file and write the xml tree output to it
        File logFile = new File(logDir.getPath() + File.separator + methodName + ".txt");
        if (!logFile.exists()) {
            logFile.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
                    logFile, true));
            bufferedWriter.write(driver.getPageSource());
            bufferedWriter.close();
        }
        
        // attach the file link to the report
        Reporter.setCurrentTestResult(result);
        Reporter.log("<a target='_blank' href='./" + logFile.getName() + "'>Application XML Tree</a>");

    }
    
    public void attachDataFeedLog(ITestResult result) throws Exception {
        
        //get all the files in the temp log directory
        File tempLogFolder = new File(pathToTempLogs);
        File[] tempLogFiles = tempLogFolder.listFiles();

        //set the file name based on date in ms and test method name
        Date date = new Date(result.getEndMillis());
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(attachmentDateFormat);
        String screenshotDateTime = dateTimeFormat.format(date);
        String fileName = "DataFeedLog" + result.getMethod().getMethodName() + "_" + screenshotDateTime;
        
        //add link to online json viewer
        if (tempLogFiles.length > 0) {
            Reporter.log("<br><a target='_blank' href='http://jsonviewer.stack.hu/'>Data Feed JSON Viewer</a>");
        }
        
        for (int i = 0; i < tempLogFiles.length; i++) {
            
            String tempLogFileName = tempLogFiles[i].getName();
            if (tempLogFileName.contains("DataFeedLog")) {
                
                //copy the file to the screenshots directory
                FileUtils.copyFile(tempLogFiles[i], new File(pathToScreenshots + fileName + i + ".txt"));
                
                //attach file to report
                int logIter = i + 1;
                Reporter.log("<a target='_blank' href='./" + fileName + i + ".txt'>Data Feed Log - " 
                    + screenshotDateTime + " - " + logIter + "</a>");
                
            }
        }
      
    }
    
    public void cleanTempLogDir() throws Exception {
        
        //get all the files in the temp log directory
        File tempLogFolder = new File(pathToTempLogs);
        File[] tempLogFiles = tempLogFolder.listFiles();

        //delete the files
        for (int i = 0; i < tempLogFiles.length; i++) {
            tempLogFiles[i].delete();
        }
        
    }
    
    public void cleanScreenshotDir() throws Exception {

        // get all the files in the temp log directory
        File screenshotFolder = new File(pathToScreenshots);
        File[] screenshotFiles = screenshotFolder.listFiles();

        // delete the files (only for the current os run)
        for (int i = 0; i < screenshotFiles.length; i++) {
        	File file = screenshotFiles[i];
        	if (file.getName().contains(mobileOS)) {
        		file.delete();
        	}
        }

    }
    
    public void cleanCharlesLogDir() throws Exception {
        
        //create the log directory if not present
        File logDir = new File(pathToCharlesLogs);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        
        //get all the files in the charles log directory
        File[] tempLogFiles = logDir.listFiles();

        //delete the files
        for (int i = 0; i < tempLogFiles.length; i++) {
            tempLogFiles[i].delete();
        }
        
    }
    
    public void cleanCharlesFailureLogDir() throws Exception {
        
        //create the log directory if not present
        File logDir = new File(pathToCharlesFailureLogs);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        
        //get all the files in the charles log directory
        File[] tempLogFiles = logDir.listFiles();

        //delete the files
        for (int i = 0; i < tempLogFiles.length; i++) {
            tempLogFiles[i].delete();
        }
        
    }
    
    public void attachCharlesFailureLog(ITestResult result) throws Exception {
        
        //get the charles dir
        File logDir = new File(pathToCharlesLogs);
        
        //get all the files in the charles log directory
        File[] tempLogFiles = logDir.listFiles();

        //set the file name based on date in ms and test method name
        Date date = new Date(result.getEndMillis());
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(attachmentDateFormat);
        String logDateTime = dateTimeFormat.format(date);
        String fileName = "CharlesFailureLog" + result.getMethod().getMethodName() + "_" + logDateTime;
        
        //add title
        if (tempLogFiles.length > 0) {
            Reporter.log("<br>Charles Failure Logs");
        }
        
        for (int i = 0; i < tempLogFiles.length; i++) {
            
            String tempLogFileName = tempLogFiles[i].getName();
            if (tempLogFileName.contains("charles")) {
                
                //copy the file to the failure dir
                FileUtils.copyFile(tempLogFiles[i], new File(pathToCharlesFailureLogs + fileName + i + ".har"));
                
                //attach file to report
                int logIter = i + 1;
                Reporter.log("<a target='_blank' href='./" + fileName + i + ".har'>Charles Failure Log - " 
                    + logDateTime + " - " + logIter + "</a>");
                
            }
        }
        
    }
    
}
