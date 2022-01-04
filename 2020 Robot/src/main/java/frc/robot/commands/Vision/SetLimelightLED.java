/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Vision;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.tracking.LimelightInterface;

public class SetLimelightLED extends CommandBase {
  /**
   * Creates a new SetLimelightLED.
   */
  private LimelightInterface mLimelightInterface;
  private int mLEDState;
  private double count;
  public SetLimelightLED(LimelightInterface limelightInterface, int ledState) {
    // Use addRequirements() here to declare subsystem dependencies.
    mLimelightInterface = limelightInterface;
    mLEDState = ledState;
    addRequirements(mLimelightInterface);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    mLimelightInterface.setLimeLightLED(mLEDState);
    count = 0.0;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    mLimelightInterface.setLimeLightLED(mLEDState);
    count++;
    System.out.println("Running Set Limelight LED");
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return count >= 5.0;
  }
}
