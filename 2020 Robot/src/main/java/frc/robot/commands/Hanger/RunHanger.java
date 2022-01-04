/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Hanger;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Hanger;

public class RunHanger extends CommandBase {
  /**
   * Creates a new RunHanger.
   */
  private Hanger mHanger;
  private DoubleSupplier mLeftY;
  public RunHanger(Hanger hanger, DoubleSupplier leftY) {
    // Use addRequirements() here to declare subsystem dependencies.
    mHanger = hanger;
    mLeftY = leftY;
    addRequirements(mHanger);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    mHanger.setElevatorSpeed(mLeftY.getAsDouble());
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    mHanger.setElevatorSpeed(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
