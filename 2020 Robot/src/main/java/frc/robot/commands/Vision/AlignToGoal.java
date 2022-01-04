/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Vision;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Drive;
import frc.robot.tracking.LimelightInterface;
import frc.robot.utilities.DaisyMath;

public class AlignToGoal extends CommandBase {
  /**
   * Creates a new AlignToGoal.
   */
  private LimelightInterface mLimelightInterface;
  private Drive mDrive;
  private BooleanSupplier mTrig;
  private DoubleSupplier mLeftY;
  private DoubleSupplier mRightX;
  //PID Values for turn
  private double kP = Constants.Vision.VISION_KP;
  private double kI = Constants.Vision.VISION_KI;
  private double kD = Constants.Vision.VISION_KD;//5;

  private double distance;
  private double speed;
  private double targetAngle;
  private double turnError;
  private double turnSlope;
  private double turnSum;
  private double lastTurnError;
  private double turn;
  private double lastTime;
  private double maxOutput;
  private double minOutput;

  public AlignToGoal(Drive drive, DoubleSupplier leftY, DoubleSupplier rightX, BooleanSupplier trig) {
    // Use addRequirements() here to declare subsystem dependencies.
    mLimelightInterface = LimelightInterface.getInstance();
    mDrive = drive;
    mLeftY = leftY;
    mRightX = rightX;
    mTrig = trig;
    addRequirements(mDrive);
    distance = 0.0;
    speed = 0.0;
    turnError = 0.0;
    turnSlope = 0.0;
    turn = 0.0;
    lastTurnError = 0.0;
    lastTime = 0.0;
    targetAngle = 0.0;
    turnSum = 0.0;
    maxOutput = 0.35;
    minOutput = 0.165;
    SmartDashboard.putNumber("Vision/kP", kP);
    SmartDashboard.putNumber("Vision/kI", kI);
    SmartDashboard.putNumber("Vision/kD", kD);
    SmartDashboard.putNumber("Vision/Max Turn Output", maxOutput);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    SmartDashboard.putNumber("Vision/kP", kP);
    SmartDashboard.putNumber("Vision/kI", kI);
    SmartDashboard.putNumber("Vision/kD", kD);
    SmartDashboard.putNumber("Vision/Max Turn Output", maxOutput);
    mLimelightInterface.setLimeLightLED(3);
    targetAngle = DaisyMath.boundAngleNeg180to180Degrees(mDrive.getGyroAngle() - mLimelightInterface.tx);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    kP = SmartDashboard.getNumber("Vision/kP", kP);
    kI = SmartDashboard.getNumber("Vision/kI", kI);
    kD = SmartDashboard.getNumber("Vision/kD", kD);
    maxOutput = SmartDashboard.getNumber("Vision/Max Turn Output", maxOutput);
    mLimelightInterface.setLimeLightLED(3);
    if (mLimelightInterface.tv > 0.0) {
      lastTurnError = turnError;
      turnSum += turnError;
      turnSum = DaisyMath.minmax(turnSum, -10000.0, 10000.0);
      turnError = DaisyMath.boundAngleNeg180to180Degrees(targetAngle - mDrive.getGyroAngle());
      turnSlope = turnError - lastTurnError;
      turn = turnError * kP + turnSum * kI + turnSlope * kD; 
      double speed = 0.0;
      if (mLeftY != null){
        speed = mLeftY.getAsDouble();
      }
      turn = Math.signum(turn) * DaisyMath.minmax(Math.abs(turn), minOutput, maxOutput);
      mDrive.getDifferentialDrive().arcadeDrive(speed, DaisyMath.minmax(-1.0 * turn, -1.0, 1.0));
    } else {
      turn = 0.0;
      speed = 0.0;
      if (mLeftY != null){
        speed = mLeftY.getAsDouble();
      }
      if (mRightX != null){
        turn = mRightX.getAsDouble();
        turn = Math.signum(turn) * DaisyMath.minmax(Math.abs(turn), minOutput, maxOutput);
      }
      mDrive.getDifferentialDrive().arcadeDrive(speed, turn);
    }
    if (turnError < 1.5) {
      targetAngle = DaisyMath.boundAngleNeg180to180Degrees(mDrive.getGyroAngle() - mLimelightInterface.tx);
    }
    logToDashBoard();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    mLimelightInterface.setLimeLightLED(1);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return !mTrig.getAsBoolean();
  }
  
  public void logToDashBoard() {
    SmartDashboard.putNumber("Vision/Turn Value", turn);
    SmartDashboard.putNumber("Vision/Turn Error", turnError);
    SmartDashboard.putNumber("Vision/Target Angle", targetAngle);
    SmartDashboard.putBoolean("Vision/Trigger Value", mTrig.getAsBoolean());
    
  }
}
