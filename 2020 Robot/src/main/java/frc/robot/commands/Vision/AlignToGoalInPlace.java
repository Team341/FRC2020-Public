/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Vision;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Drive;
import frc.robot.tracking.LimelightInterface;

public class AlignToGoalInPlace extends CommandBase {
  /**
   * Creates a new AlignToGoal.
   */
  private LimelightInterface mLimelightInterface;
  private Drive mDrive;
  private RobotContainer mRobotContainer;
  private DoubleSupplier mLeftY;
  //PID Values for turn
  private double kPTurn = 1.0 / 18.0;
  private double kITurn = 0.05;
  private double kDTurn = 0.35;//5;

  private double distance;
  private double speed;
  private double turnError;
  private double turnSlope;
  private double lastTurnError;
  private double turn;
  private double lastTime;

  public AlignToGoalInPlace(Drive drive) {
    // Use addRequirements() here to declare subsystem dependencies.
    mLimelightInterface = LimelightInterface.getInstance();
    mDrive = drive;
    //mRobotContainer = RobotContainer.getInstance();
    addRequirements(mDrive);
    distance = 0.0;
    speed = 0.0;
    turnError = 0.0;
    turnSlope = 0.0;
    turn = 0.0;
    lastTurnError = 0.0;
    lastTime = 0.0;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    lastTurnError = turnError;
    if (mLimelightInterface.tv > 0.0) {
      turnError = mLimelightInterface.tx;
      turnSlope = turnError - lastTurnError;
      turn = turnError * kPTurn + turnSlope * kDTurn; 
      mDrive.getDifferentialDrive().arcadeDrive(0.0, turn);
    } else {
      mDrive.getDifferentialDrive().arcadeDrive(0.0, 0.0);
    }
    System.out.println("Turn Error: " + turnError);
    System.out.println("Turn Slope: " + turnSlope);
    System.out.println("Turn: " + turn);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    mDrive.getDifferentialDrive().arcadeDrive(0.0, 0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return mLimelightInterface.alignedToGoal();
  }
}
