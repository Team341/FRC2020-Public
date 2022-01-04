/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.commands.Drive.ResetOdometry;
import frc.robot.subsystems.LEDs;
import frc.robot.tracking.LimelightInterface;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private RobotContainer m_robotContainer;
  private LimelightInterface mLimelightInterface;


  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();
    
    SmartDashboard.putBoolean("Robot Enabled", false);
    mLimelightInterface = LimelightInterface.getInstance();
    // mLimelightInterface.setLimeLightLED(3);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    mLimelightInterface.run();
    CommandScheduler.getInstance().run();
  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   */
  @Override
  public void disabledInit() {
    m_robotContainer.getLEDs().setShowPattern();
    mLimelightInterface.setLimeLightLED(1);
    SmartDashboard.putBoolean("Robot Enabled", false);
  }

  @Override
  public void disabledPeriodic() {
    mLimelightInterface.setLimeLightLED(1);
  }

  /**
   * This autonomous runs the autonomous command selected by your {@link RobotContainer} class.
   */
  @Override
  public void autonomousInit() {
  
    // m_robotContainer.mDrive.resetGyroCompletely();
    m_robotContainer.mDrive.resetOdometry();
    
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    m_robotContainer.mDrive.setBrakeMode();
    m_robotContainer.mDrive.resetEncoders();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
    SmartDashboard.putBoolean("Robot Enabled", true);
    mLimelightInterface.setLimeLightLED(3);
    
    SmartDashboard.putBoolean("Shooter/Manual RPM Control", false);
    
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    // CommandScheduler.getInstance().run();        // TODO: <- Is this needed????????
    // m_robotContainer.testPath1.logToDashboard();
    mLimelightInterface.setLimeLightLED(3);
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.

    m_robotContainer.mDrive.setCoastMode();

    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
    // m_robotContainer.getLEDs().setShooterLEDColorForAll(Constants.LEDs.OFF_RGB);
    SmartDashboard.putBoolean("Robot Enabled", true);
    // mLimelightInterface.setLimeLightLED(3);
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
