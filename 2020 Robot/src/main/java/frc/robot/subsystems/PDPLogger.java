/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


/**
 * Add your docs here.
 */
public class PDPLogger extends SubsystemBase {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  PowerDistributionPanel mPDP;

  private double mTotalCurrent;

  //sets instance as a new Tower, and will return that when called
  private static PDPLogger instance = null;
  public static PDPLogger getInstance() {
    if (instance == null) {
      instance = new PDPLogger();
    }
    return instance;
  }

  public PDPLogger() {
    mPDP = new PowerDistributionPanel(0);
    mTotalCurrent = 0.0;
  }

  public double getVoltage() {
    return mPDP.getVoltage();
  }

  public double getTotalCurrent() {
    return mPDP.getTotalPower();
  }

  public double getChannelCurrent(int port) {
    return mPDP.getCurrent(port);
  }

  public void logToDashboard() {
    SmartDashboard.putNumber("PDP/Voltage", getVoltage());
    SmartDashboard.putNumber("PDP/Total Current", getTotalCurrent());

    for(int n=0;n<16;n++){
      SmartDashboard.putNumber("PDP/Current Channel [" + n + "]", getChannelCurrent(n));
    }
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    logToDashboard();
  }


}
