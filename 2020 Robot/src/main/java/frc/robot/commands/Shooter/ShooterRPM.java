/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Shooter;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Tower;
import frc.robot.tracking.LimelightInterface;
import frc.robot.utilities.DaisyMath;

public class ShooterRPM extends CommandBase {
  /**
   * Creates a new ShooterRPM.
   */

  private Shooter mShooter;
  private Tower mTower;
  private LimelightInterface mLimelightInterface;
  private BooleanSupplier mTrigLeft;
  private boolean mIsManual;
  private double rpm;
  private double calculatedSpeed;
  private double lastError;
  private double errorSum;
  private double kP = 0.003;
  private double kI = 0.0;
  private double kD = 0.0;
  private double kF = 1.0 / 7200.0;
  // private double rpmThreshold;
  private boolean mEnableLimelight;
  private int countsSinceShot = 1000000;
  private double rpmBoost = Constants.Shooter.RPM_BOOST;

  public ShooterRPM(Shooter shooter, Tower tower, BooleanSupplier trigLeft, boolean enableLimelight) {
    // Use addRequirements() here to declare subsystem dependencies.
    mShooter = shooter;
    mTower = tower;
    mLimelightInterface = LimelightInterface.getInstance();
    mTrigLeft = trigLeft;
    rpm = 2000.0;
    calculatedSpeed = 0.0;
    lastError = 0.0;
    errorSum = 0.0;
    // rpmThreshold = 50.0;
    mIsManual = false;
    mEnableLimelight = enableLimelight;
    countsSinceShot = 1000000;
    SmartDashboard.putNumber("Shooter/kP", kP);
    SmartDashboard.putNumber("Shooter/kI", kI);
    SmartDashboard.putNumber("Shooter/kD", kD);
    SmartDashboard.putNumber("Shooter/RPM Boost", rpmBoost);
    SmartDashboard.putBoolean("Shooter/Manual RPM Control", false);
  }
  

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    countsSinceShot = 1000000;
    mIsManual = SmartDashboard.getBoolean("Shooter/Manual RPM Control", false);
    if(mIsManual) {
      rpm = SmartDashboard.getNumber("Shooter/RPM SetPoint", rpm);
    } else {
      rpm = mShooter.getRPMFromTable(mLimelightInterface.getDistance());
    }
    if (mEnableLimelight) {
      mLimelightInterface.setLimeLightLED(3);
    }
    // rpmThreshold = SmartDashboard.getNumber("Shooter/RPM Threshold", rpmThreshold);
    kP = SmartDashboard.getNumber("Shooter/kP", kP);
    kI = SmartDashboard.getNumber("Shooter/kI", kI);
    kD = SmartDashboard.getNumber("Shooter/kD", kD);
    rpmBoost = SmartDashboard.getNumber("Shooter/RPM Boost", rpmBoost);
    calculatedSpeed = calculateOutput(rpm, mShooter.getRPM());
    if (mTrigLeft.getAsBoolean()) {
      mShooter.setSpeed(calculatedSpeed);
    } else {
      mShooter.setSpeed(0.0);
    }
    logToDashboard();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (mTower.getBeamBreakOneState()){
      countsSinceShot = 1000000;
    } else {
      countsSinceShot++;
    }

    if(mIsManual) {
      rpm = SmartDashboard.getNumber("Shooter/RPM SetPoint", rpm);
    } else {
      rpm = mShooter.getRPMFromTable(mLimelightInterface.getDistance());
    }
    if (countsSinceShot <= Constants.Shooter.RPM_BOOST_CYCLE_COUNT) { 
      // The ball is considered "shot" after it clears the top break beam. It is expected there is some loss in rpm and it needs to be sped back up. 
      // But if we want to rapid fire, it will be too slow, so bump the rpm up a little to try and get it back to optimal speed sooner.
      rpm += rpmBoost;
    }

    if (mEnableLimelight) {
      mLimelightInterface.setLimeLightLED(3);
    }
    // rpmThreshold = SmartDashboard.getNumber("Shooter/RPM Threshold", rpmThreshold);
    // kP = SmartDashboard.getNumber("Shooter/kP", kP);
    // kI = SmartDashboard.getNumber("Shooter/kI", kI);
    // kD = SmartDashboard.getNumber("Shooter/kD", kD);
    calculatedSpeed = calculateOutput(rpm, mShooter.getRPM());
    if (mTrigLeft.getAsBoolean()) {
      mShooter.setSpeed(calculatedSpeed);
    } else {
      mShooter.setSpeed(0.0);
    }
    logToDashboard();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    mShooter.setSpeed(0.0);
    mLimelightInterface.setLimeLightLED(1);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return !mTrigLeft.getAsBoolean();
  }

  public double calculateOutput(double goal, double current) {
    double error = goal - current;
    mShooter.setDifferenceBetweenRPMAndGoal(error);
    double errorDiff = error - lastError;
    errorSum += error;
    errorSum = DaisyMath.minmax(errorSum, -10000.0, 10000.0);
    lastError = error;
    if (current <= Constants.Shooter.BASE_RPM_SPEED) {
      // Don't rely on PID until after we have spun up to some minimum RPM, this is to try and get desired speed faster from stopped position
      return 1.0;
    } else {
      return DaisyMath.minmax(kF * goal + kP * error + kI * errorSum + kD * errorDiff, 0.0, 1.0);
    }
  }

  private void logToDashboard() {
    SmartDashboard.putNumber("Shooter/Calculated Speed", calculatedSpeed);
    SmartDashboard.putNumber("Shooter/RPM in Command", rpm);
    SmartDashboard.putNumber("Shooter/kP", kP);
    SmartDashboard.putNumber("Shooter/kI", kI);
    SmartDashboard.putNumber("Shooter/kD", kD);
    SmartDashboard.putBoolean("Shooter/Trig Left", mTrigLeft.getAsBoolean());
  }
}
