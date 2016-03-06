package com.profesorfalken.jpowershell;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for jPowerShell
 *
 * @author Javier Garcia Alonso
 */
public class PowerShellTest {

    /**
     * Test of openSession method, of class PowerShell.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testListDir() throws Exception {
        System.out.println("testListDir");
        if (OSDetector.isWindows()) {
            PowerShell powerShell = PowerShell.openSession();
            PowerShellResponse response = powerShell.executeCommand("dir");

            System.out.println("List Directory:" + response.getCommandOutput());

            Assert.assertTrue(response.getCommandOutput().contains("LastWriteTime"));

            powerShell.close();
        }
    }

    /**
     * Test of openSession method, of class PowerShell.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSimpleListDir() throws Exception {
        System.out.println("start testListDir");
        if (OSDetector.isWindows()) {

            PowerShellResponse response = PowerShell.executeSingleCommand("dir");

            System.out.println("List Directory:" + response.getCommandOutput());

            Assert.assertTrue(response.getCommandOutput().contains("LastWriteTime"));
        }
        System.out.println("end testListDir");
    }

    /**
     * Test of openSession method, of class PowerShell.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testListProcesses() throws Exception {
        System.out.println("testListProcesses");
        if (OSDetector.isWindows()) {
            PowerShell powerShell = PowerShell.openSession();
            PowerShellResponse response = powerShell.executeCommand("Get-Process");

            System.out.println("List Processes:" + response.getCommandOutput());

            Assert.assertTrue(response.getCommandOutput().contains("powershell"));

            powerShell.close();
        }
    }

    /**
     * Test of openSession method, of class PowerShell.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCheckBIOSByWMI() throws Exception {
        System.out.println("testCheckBIOSByWMI");
        if (OSDetector.isWindows()) {
            PowerShell powerShell = PowerShell.openSession();
            PowerShellResponse response = powerShell.executeCommand("Get-WmiObject Win32_BIOS");
            System.out.println("Check BIOS:" + response.getCommandOutput());

            Assert.assertTrue(response.getCommandOutput().contains("SMBIOSBIOSVersion"));

            powerShell.close();
        }
    }

    /**
     * Test of empty response
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCheckEmptyResponse() throws Exception {
        System.out.println("testCheckEmptyResponse");
        if (OSDetector.isWindows()) {
            PowerShell powerShell = PowerShell.openSession();
            PowerShellResponse response = powerShell.executeCommand("Get-WmiObject Win32_1394Controller");
            System.out.println("Empty response:" + response.getCommandOutput());

            Assert.assertTrue("".equals(response.getCommandOutput()));

            powerShell.close();
        }
    }

    /**
     * Test of long command
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLongCommand() throws Exception {
        System.out.println("testLongCommand");
        if (OSDetector.isWindows()) {
            PowerShell powerShell = PowerShell.openSession();
            PowerShellResponse response = powerShell.executeCommand("Get-WMIObject -List | Where{$_.name -match \"^Win32_\"} | Sort Name");
            System.out.println("Long list:" + response.getCommandOutput());

            Assert.assertTrue(response.getCommandOutput().length() > 1000);

            powerShell.close();
        }
    }

    /**
     * Test error case.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testErrorCase() throws Exception {
        System.out.println("testErrorCase");
        if (OSDetector.isWindows()) {
            PowerShell powerShell = PowerShell.openSession();
            PowerShellResponse response = powerShell.executeCommand("sfdsfdsf");
            System.out.println("Error:" + response.getCommandOutput());

            Assert.assertTrue(response.getCommandOutput().contains("sfdsfdsf"));

            powerShell.close();
        }
    }

    /**
     * Test of openSession method, of class PowerShell.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testMultipleCalls() throws Exception {
        System.out.println("testMultiple");
        if (OSDetector.isWindows()) {
            PowerShell powerShell = PowerShell.openSession();
            PowerShellResponse response = powerShell.executeCommand("dir");
            System.out.println("First call:" + response.getCommandOutput());
            Assert.assertTrue("Cannot find LastWriteTime", response.getCommandOutput().contains("LastWriteTime"));
            response = powerShell.executeCommand("Get-Process");
            System.out.println("Second call:" + response.getCommandOutput());
            Assert.assertTrue("Cannot find powershell", response.getCommandOutput().contains("powershell"));
            response = powerShell.executeCommand("Get-WmiObject Win32_BIOS");
            System.out.println("Third call:" + response.getCommandOutput());
            Assert.assertTrue("Cannot find SMBIOSBIOSVersion", response.getCommandOutput().contains("SMBIOSBIOSVersion"));

            powerShell.close();
        }
    }

    /**
     * Test loop.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLoop() throws Exception {
        System.out.println("testLoop");
        if (OSDetector.isWindows()) {
            PowerShell powerShell = null;
            try {
                powerShell = PowerShell.openSession();
                for (int i = 0; i < 10; i++) {
                    System.out.print("Cycle: " + i);
                    //Thread.sleep(3000);

                    String output = powerShell.executeCommand("date").getCommandOutput().trim();

                    System.out.println("\t" + output);
                }
            } catch (PowerShellNotAvailableException ex) {
                //Handle error when PowerShell is not available in the system
                //Maybe try in another way?
            } finally {
                //Always close PowerShell session to free resources.
                if (powerShell != null) {
                    powerShell.close();
                }
            }
        }
    }

    /**
     * Test long loop.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testLongLoop() throws Exception {
        System.out.println("testLongLoop");
        if (OSDetector.isWindows()) {
            PowerShell powerShell = null;
            try {
                powerShell = PowerShell.openSession();
                for (int i = 0; i < 100; i++) {
                    System.out.print("Cycle: " + i);

                    //Thread.sleep(100);

                    PowerShellResponse response = powerShell.executeCommand("date"); // Line 17 (see exception below)

                    if (response.isError()) {
                        System.out.println("error"); // never called
                    }

                    String output = "<" + response.getCommandOutput().trim() + ">";

                    System.out.println("\t" + output);
                }
            } catch (PowerShellNotAvailableException ex) {
                //Handle error when PowerShell is not available in the system
                //Maybe try in another way?
            } finally {
                //Always close PowerShell session to free resources.
                if (powerShell != null) {
                    powerShell.close();
                }
            }
        }
    }
}
