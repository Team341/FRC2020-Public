/**
 * This is a very simple robot program that can be used to send telemetry to
 * the data_logger script to characterize your drivetrain. If you wish to use
 * your actual robot code, you only need to implement the simple logic in the
 * autonomousPeriodic function and change the NetworkTables update rate
 */

package dc;

import java.util.function.Supplier;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder.IndexingType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Units;

public class Robot extends TimedRobot {

  static private double WHEEL_DIAMETER = 0.1524;
  static private double GEARING = 12.27;
  static private int PIDIDX = 0;

  Joystick stick;
  DifferentialDrive drive;

  CANSparkMax leftMaster;
  CANSparkMax rightMaster;

  Encoder leftEncoder;
  Encoder rightEncoder;

  Supplier<Double> leftEncoderPosition;
  Supplier<Double> leftEncoderRate;
  Supplier<Double> rightEncoderPosition;
  Supplier<Double> rightEncoderRate;
  Supplier<Double> gyroAngleRadians;

  NetworkTableEntry autoSpeedEntry =
      NetworkTableInstance.getDefault().getEntry("/robot/autospeed");
  NetworkTableEntry telemetryEntry =
      NetworkTableInstance.getDefault().getEntry("/robot/telemetry");
  NetworkTableEntry rotateEntry =
    NetworkTableInstance.getDefault().getEntry("/robot/rotate");

  double priorAutospeed = 0;
  Number[] numberArray = new Number[10];

  @Override
  public void robotInit() {
    if (!isReal()) SmartDashboard.putData(new SimEnabler());

    stick = new Joystick(0);

    leftMaster = new CANSparkMax(23, MotorType.kBrushless);
    leftMaster.setInverted(false);
    leftMaster.setIdleMode(IdleMode.kBrake);

    leftEncoder = new Encoder(0, 1, true, EncodingType.k1X);
    // leftEncoder.setReverseDirection(true);
    leftEncoder.setSamplesToAverage(7);

    rightMaster = new CANSparkMax(10, MotorType.kBrushless);
    rightMaster.setInverted(false);
    rightMaster.setIdleMode(IdleMode.kBrake);

    rightEncoder = new Encoder(2, 3, false, EncodingType.k1X);
    // rightEncoder.setReverseDirection(false);

    CANSparkMax leftSlave0 = new CANSparkMax(24, MotorType.kBrushless);
    leftSlave0.follow(leftMaster);
    leftSlave0.setIdleMode(IdleMode.kBrake);
    CANSparkMax leftSlave1 = new CANSparkMax(25, MotorType.kBrushless);
    leftSlave1.follow(leftMaster);
    leftSlave1.setIdleMode(IdleMode.kBrake);

    CANSparkMax rightSlave0 = new CANSparkMax(11, MotorType.kBrushless);
    rightSlave0.follow(rightMaster);
    rightSlave0.setIdleMode(IdleMode.kBrake);
    CANSparkMax rightSlave1 = new CANSparkMax(12, MotorType.kBrushless);
    rightSlave1.follow(rightMaster);
    rightSlave1.setIdleMode(IdleMode.kBrake);

    rightEncoder.setSamplesToAverage(7);

    //
    // Configure gyro
    //

    // Note that the angle from the NavX and all implementors of wpilib Gyro
    // must be negated because getAngle returns a clockwise positive angle
    AHRS navx = new AHRS(SPI.Port.kMXP);
    gyroAngleRadians = () -> Math.toRadians(navx.getAngle());

    //
    // Configure drivetrain movement
    //

    drive = new DifferentialDrive(leftMaster, rightMaster);

    drive.setDeadband(0);

    //
    // Configure encoder related functions -- getDistance and getrate should
    // return units and units/s
    //

    double encoderConstant =
        (1 / GEARING) * WHEEL_DIAMETER * Math.PI;

    // leftEncoder.setDistancePerPulse(Units.inchesToMeters(4.0 * Math.PI / 256.0));
    // rightEncoder.setDistancePerPulse(Units.inchesToMeters(4.0 * Math.PI / 256.0));
    
    leftEncoder.setDistancePerPulse(6.0 * Math.PI / 2048.0);
    rightEncoder.setDistancePerPulse(6 * Math.PI / 2048.0);
    
    leftEncoderPosition = ()
        -> Units.inchesToMeters(leftEncoder.getDistance());
    leftEncoderRate = ()
        -> Units.inchesToMeters(leftEncoder.getRate());

    rightEncoderPosition = ()
        -> Units.inchesToMeters(rightEncoder.getDistance());
    rightEncoderRate = ()
        -> Units.inchesToMeters(rightEncoder.getRate());
    // Reset encoders
    leftEncoder.reset();
    rightEncoder.reset();

    // Set the update rate instead of using flush because of a ntcore bug
    // -> probably don't want to do this on a robot in competition
    NetworkTableInstance.getDefault().setUpdateRate(0.010);
  }

  @Override
  public void disabledInit() {
    System.out.println("Robot disabled");
    drive.tankDrive(0, 0);
    
    leftEncoder.reset();
    rightEncoder.reset();
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void robotPeriodic() {
    // feedback for users, but not used by the control program
    SmartDashboard.putNumber("l_encoder_pos", leftEncoderPosition.get());
    SmartDashboard.putNumber("l_encoder_rate", leftEncoderRate.get());
    SmartDashboard.putNumber("r_encoder_pos", rightEncoderPosition.get());
    SmartDashboard.putNumber("r_encoder_rate", rightEncoderRate.get());
  }

  @Override
  public void teleopInit() {
    System.out.println("Robot in operator control mode");
  }

  @Override
  public void teleopPeriodic() {
    drive.arcadeDrive(-stick.getY(), stick.getX());
  }

  @Override
  public void autonomousInit() {
    System.out.println("Robot in autonomous mode");
  }

  /**
   * If you wish to just use your own robot program to use with the data logging
   * program, you only need to copy/paste the logic below into your code and
   * ensure it gets called periodically in autonomous mode
   *
   * Additionally, you need to set NetworkTables update rate to 10ms using the
   * setUpdateRate call.
   */
  @Override
  public void autonomousPeriodic() {

    // Retrieve values to send back before telling the motors to do something
    double now = Timer.getFPGATimestamp();

    double leftPosition = leftEncoderPosition.get();
    double leftRate = leftEncoderRate.get();

    double rightPosition = rightEncoderPosition.get();
    double rightRate = rightEncoderRate.get();

    double battery = RobotController.getBatteryVoltage();

    double leftMotorVolts = leftMaster.getBusVoltage() * leftMaster.getAppliedOutput();
    double rightMotorVolts = rightMaster.getBusVoltage() * rightMaster.getAppliedOutput();

    // Retrieve the commanded speed from NetworkTables
    double autospeed = autoSpeedEntry.getDouble(0);
    priorAutospeed = autospeed;

    // command motors to do things
    drive.tankDrive(
      (rotateEntry.getBoolean(false) ? -1 : 1) * autospeed, autospeed,
      false
    );

    // send telemetry data array back to NT
    numberArray[0] = now;
    numberArray[1] = battery;
    numberArray[2] = autospeed;
    numberArray[3] = leftMotorVolts;
    numberArray[4] = rightMotorVolts;
    numberArray[5] = leftPosition;
    numberArray[6] = rightPosition;
    numberArray[7] = leftRate;
    numberArray[8] = rightRate;
    numberArray[9] = gyroAngleRadians.get();

    telemetryEntry.setNumberArray(numberArray);
  }
}
