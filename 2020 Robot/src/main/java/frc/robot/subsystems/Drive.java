/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Units;
import frc.robot.utilities.DaisyMath;
import edu.wpi.first.wpilibj.SPI;

public class Drive extends SubsystemBase {
  /**
   * Creates a new Drive.
   */

  private static Drive instance = null;
  public static Drive getInstance() {
    if(instance == null) {
      instance = new Drive();
    }
    return instance;
  }

  // TODO update turn command thing to be when held

  // initializes left motors
  private final CANSparkMax mLeftMotorOne;
  private final CANSparkMax mLeftMotorTwo;
  private final CANSparkMax mLeftMotorThree;

  // initializes right motors
  private final CANSparkMax mRightMotorOne;
  private final CANSparkMax mRightMotorTwo;
  private final CANSparkMax mRightMotorThree;

  // creates SpeedControllerGroup instances for motors
  // allows for motors to be passed methods in a group without the use of master/slave
  // used to pass into differential drive class for simple drive methods
  private final SpeedControllerGroup mLeftMotors;
  private final SpeedControllerGroup mRightMotors;
  
  // creates new instance of DifferentialDrive class for SpeedControllerGroup instances
  // allows for easy creation of drive type methods given left and right motor groups
  // https://first.wpi.edu/FRC/roborio/beta/docs/java/edu/wpi/first/wpilibj/drive/DifferentialDrive.html
  // extensibly equivalent to RobotDrive.tankDrive(double, double) if a deadband of 0 is used
  private final DifferentialDrive mDifferentialDrive;

  private AHRS mGyro;
  private double mGyroOffset, mPitchOffset;
  private final DifferentialDriveOdometry mOdometry;
  private final DifferentialDriveOdometry mInvertedOdometry;

  private final Solenoid mPopper;

  private final SendableChooser<Boolean> mTurnDampening;

  // creates new encoder objects
  private final Encoder mAltEncoderLeft;
  private final Encoder mAltEncoderRight;

  private final double halfSpeedScalar = 1.0;
  
  public Drive() {

    // Setup the left side drive motors (3x Neo)
    mLeftMotorOne = new CANSparkMax(Constants.Drive.LEFT_MOTOR_A_PORT, MotorType.kBrushless);
    mLeftMotorTwo = new CANSparkMax(Constants.Drive.LEFT_MOTOR_B_PORT, MotorType.kBrushless);
    mLeftMotorThree = new CANSparkMax(Constants.Drive.LEFT_MOTOR_C_PORT, MotorType.kBrushless);
    
    // Configure the left side drive motors
    mLeftMotorOne.restoreFactoryDefaults();
    mLeftMotorTwo.restoreFactoryDefaults();
    mLeftMotorThree.restoreFactoryDefaults();
    mLeftMotorOne.setSmartCurrentLimit(Constants.Drive.DRIVE_MOTOR_CURRENT_LIMIT);
    mLeftMotorTwo.setSmartCurrentLimit(Constants.Drive.DRIVE_MOTOR_CURRENT_LIMIT);
    mLeftMotorThree.setSmartCurrentLimit(Constants.Drive.DRIVE_MOTOR_CURRENT_LIMIT);
    mLeftMotorOne.setIdleMode(IdleMode.kCoast);
    mLeftMotorTwo.setIdleMode(IdleMode.kCoast);
    mLeftMotorThree.setIdleMode(IdleMode.kCoast);

    // mLeftMotorOne.setOpenLoopRampRate(0.25);
    // mLeftMotorTwo.setOpenLoopRampRate(0.25);
    // mLeftMotorThree.setOpenLoopRampRate(0.25);


    // Burn the configuration to flash memory to prevent issues if power is lost while operating
    mLeftMotorOne.burnFlash();
    mLeftMotorTwo.burnFlash();
    mLeftMotorThree.burnFlash();

    // Setup the right side drive motors (3x Neo)
    mRightMotorOne = new CANSparkMax(Constants.Drive.RIGHT_MOTOR_A_PORT, MotorType.kBrushless);
    mRightMotorTwo = new CANSparkMax(Constants.Drive.RIGHT_MOTOR_B_PORT, MotorType.kBrushless);
    mRightMotorThree = new CANSparkMax(Constants.Drive.RIGHT_MOTOR_C_PORT, MotorType.kBrushless);
    
    // Configure the right side drive motors
    mRightMotorOne.restoreFactoryDefaults();
    mRightMotorTwo.restoreFactoryDefaults();
    mRightMotorThree.restoreFactoryDefaults();
    mRightMotorOne.setSmartCurrentLimit(Constants.Drive.DRIVE_MOTOR_CURRENT_LIMIT);
    mRightMotorTwo.setSmartCurrentLimit(Constants.Drive.DRIVE_MOTOR_CURRENT_LIMIT);
    mRightMotorThree.setSmartCurrentLimit(Constants.Drive.DRIVE_MOTOR_CURRENT_LIMIT);
    mRightMotorOne.setIdleMode(IdleMode.kCoast);
    mRightMotorTwo.setIdleMode(IdleMode.kCoast);
    mRightMotorThree.setIdleMode(IdleMode.kCoast);

    // mRightMotorOne.setOpenLoopRampRate(0.25);
    // mRightMotorTwo.setOpenLoopRampRate(0.25);
    // mRightMotorThree.setOpenLoopRampRate(0.25);

    // Burn the configuration to flash memory to prevent issues if power is lost while operating
    mRightMotorOne.burnFlash();
    mRightMotorTwo.burnFlash();
    mRightMotorThree.burnFlash();

    // Place the left motors into a speed controller group to simplify calls. Whatever value is 
    // set to this group is sent to each of the speed controllers a part of this group
    mLeftMotors =
        new SpeedControllerGroup(mLeftMotorOne,
                                 mLeftMotorTwo,
                                 mLeftMotorThree);
    
    // Place the right motors into a speed controller group to simplify calls. Whatever value is 
    // set to this group is sent to each of the speed controllers a part of this group
    mRightMotors =
        new SpeedControllerGroup(mRightMotorOne,
                                 mRightMotorTwo,
                                 mRightMotorThree);

    // mRightMotors.setInverted(true);    // Not needed, the Differential drive handles the inversion of the right side motors

    // instantiates DifferentialDrive class with left and right speed controllers
    mDifferentialDrive = new DifferentialDrive(mLeftMotors, mRightMotors);

    // TODO: Is this actually doing anything? should it be removed?
    mTurnDampening = new SendableChooser<Boolean>();
    Shuffleboard.getTab("Driver Choices").add(mTurnDampening);

    // Create the solenoid that controls the Popper pistons
    mPopper = new Solenoid(Constants.Drive.POPPER_PORT);

    // Setup the alternate (REV Through Bore) drive encoders
    mAltEncoderLeft = new Encoder(Constants.Drive.LEFT_ENCODER_A_PORT, Constants.Drive.LEFT_ENCODER_B_PORT, true, EncodingType.k2X);
    mAltEncoderRight = new Encoder(Constants.Drive.RIGHT_ENCODER_A_PORT, Constants.Drive.RIGHT_ENCODER_B_PORT, false, EncodingType.k2X);
    mAltEncoderLeft.setDistancePerPulse(Constants.Drive.DISTANCE_PER_ENCODER_PULSE);
    mAltEncoderRight.setDistancePerPulse(Constants.Drive.DISTANCE_PER_ENCODER_PULSE);

    // Setup the NavX MXP gyro
    try {
      /* Communicate w/navX MXP via the MXP SPI Bus. */
      /* Alternatively: I2C.Port.kMXP, SerialPort.Port.kMXP or SerialPort.Port.kUSB */
      /* See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/ for details. */
      mGyro = new AHRS(SPI.Port.kMXP);

    } catch (final RuntimeException ex) {
      DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
    }
    mGyroOffset = 0.0;

    // Used primarily during autonomous to determine robot position
    // It does this by integrating the change in values from the encoders, as well as using the gyro
    mOdometry = new DifferentialDriveOdometry(Rotation2d.fromDegrees(getGyroAngle()));
    mInvertedOdometry = new DifferentialDriveOdometry(Rotation2d.fromDegrees(getGyroAngle())); 
  }

  /**
   * sets the double values for both left and right motors
   * @param leftSpeed
   * @param rightSpeed
   */
  public void SetSpeed(final double leftSpeed, final double rightSpeed) {
    mLeftMotors.set(leftSpeed);
    mRightMotors.set(rightSpeed);
  }  

  /**
   * gets the x and y pose of the robot relative to the start
   * @return the post of the robot in meters
   */
  public Pose2d getPose() {
    return mOdometry.getPoseMeters();
  }

  /**
   * sets the robot drive speed in volts, necessary for advanced autonomous
   * @param leftVolts
   * @param rightVolts
   */
  public void setTankDriveVolts(final double leftVolts, final double rightVolts) {
    mLeftMotors.setVoltage(leftVolts);
    mRightMotors.setVoltage(-1.0 * rightVolts);

    // tells the motors they are connected to prevent warnings
    mDifferentialDrive.feed();
  }

    /**
   * sets the robot drive speed in volts, necessary for advanced autonomous
   * @param leftVolts
   * @param rightVolts
   */
  public void setInvertedTankDriveVolts(final double leftVolts, final double rightVolts) {
    mLeftMotors.setVoltage(-1.0 * rightVolts);
    mRightMotors.setVoltage(leftVolts);

    // tells the motors they are connected to prevent warnings
    mDifferentialDrive.feed();
  }

  /**
   * gets the wheel speeds in a differential drive based format. this is necessary for advanced autonomous. 
   * contains the left and right velocities for the wheels.
   * @return the wheel speeds of both the left and right motors in meters/sec
   */
  public DifferentialDriveWheelSpeeds getDifferentialDriveSpeed() {
    // The encoder getRate() returns the current rate of the encoder. Units are distance per second as scaled by the value from setDistancePerPulse().
    return new DifferentialDriveWheelSpeeds(Units.inchesToMeters(mAltEncoderLeft.getRate()), Units.inchesToMeters(mAltEncoderRight.getRate()));
  }

  /**
   * gets the wheel speeds in a differential drive based format. this is necessary for advanced autonomous. 
   * contains the left and right velocities for the wheels.
   * @return the wheel speeds of both the left and right motors
   */
  public DifferentialDriveWheelSpeeds getInvertedDifferentialDriveSpeed() {
    return new DifferentialDriveWheelSpeeds(Units.inchesToMeters(-1.0 * mAltEncoderRight.getRate()), -1.0 * Units.inchesToMeters(mAltEncoderLeft.getRate()));
  }
  
  /**
   * gets the double value of the robot's pose relative to power on / gyro reset
   * @return the rotation of the robot in degrees
   */
  public double getRotationInDegrees() {
    return getPose().getRotation().getDegrees();
  }

  /**
   * 
   */
  public Rotation2d getHeading() {
    return Rotation2d.fromDegrees(mGyro.getAngle());
  }

  /**
   * Returns the turn rate of the robot.
   *
   * @return The turn rate of the robot, in degrees per second
   */
  public double getTurnRate() {
    return -1.0 * mGyro.getRate();
  }

  /**
>>>>>>> Stashed changes
   * gets the double value of the robot's x movement relative to power on / gyro reset
   * @return the x component of the robot's translation
   */
  public double getTranslationX() {
    return getPose().getTranslation().getX();
  }

  /**
   * gets the double value of the robot's y movement relative to power on / gyro reset
   * @return the y component of the robot's translation
   */
  public double getTranslationY() {
    return getPose().getTranslation().getY();
  }

  /**
   * sets the boolean state of the left and right poppers
   * @param state
   */
  public void setPopperState(final boolean state) {
    mPopper.set(state);
  }

  /**
   * gets the boolean value of the popper's state
   * @return the left popper
   */
  public boolean getPopperState() {
    return mPopper.get();
  }

  /**
   * sets the speeds of both the right and the left motors
   * @param leftSpeed the double value of the left motors' speed
   * @param rightSpeed the double value of the right motors' speed
   */
  public void setSpeed(final double leftSpeed, final double rightSpeed) {
    mLeftMotors.set(leftSpeed);
    mRightMotors.set(rightSpeed);
  }

  /**
   * gets the percent value of the left drive speed
   * @return the left motors
   */
  public double getLeftDriveSpeedPercent() {
    return mLeftMotors.get();
  }

  /**
   * gets the percent value of the right drive speed
   * @return
   */
  public double getRightDriveSpeedPercent() {
    return mRightMotors.get();
  }

  public void setBrakeMode() {
    mLeftMotorOne.setIdleMode(IdleMode.kBrake);
    mLeftMotorTwo.setIdleMode(IdleMode.kBrake);
    mLeftMotorThree.setIdleMode(IdleMode.kBrake);

    mRightMotorOne.setIdleMode(IdleMode.kBrake);
    mRightMotorTwo.setIdleMode(IdleMode.kBrake);
    mRightMotorThree.setIdleMode(IdleMode.kBrake);
  }

  public void setCoastMode() {
    mLeftMotorOne.setIdleMode(IdleMode.kCoast);
    mLeftMotorTwo.setIdleMode(IdleMode.kCoast);
    mLeftMotorThree.setIdleMode(IdleMode.kCoast);

    mRightMotorOne.setIdleMode(IdleMode.kCoast);
    mRightMotorTwo.setIdleMode(IdleMode.kCoast);
    mRightMotorThree.setIdleMode(IdleMode.kCoast);
  }

  /**
   * gets distance for the left distance motor from encoder in inches
   * @return mAltEncoderLeft.getDistance()
   */
  public double getLeftDistanceInches(){
    return mAltEncoderLeft.getDistance();
  }

  /**
   * gets distance for the right distance motor from encoder in inches
   * @return mAltEncoderRight.getDistance()
   */
  public double getRightDistanceInches(){
    return mAltEncoderRight.getDistance();
  }

  /**
   * gets distance for the left distance motor from encoder in meters
   * @return mAltEncoderLeft.getDistance()
   */
  public double getLeftDistanceMeters(){
    return Units.inchesToMeters(mAltEncoderLeft.getDistance());
  }

  /**
   * gets distance for the right distance motor from encoder in meters
   * @return mAltEncoderRight.getDistance()
   */
  public double getRightDistanceMeters(){
    return Units.inchesToMeters(mAltEncoderRight.getDistance());
  }

  /**
   * get the boolean value for turn dampening
   * @return boolean is turn dampening enabled
   */
  public boolean getTurnDampening() {
    return mTurnDampening.getSelected();
  }

  /**
   * resets all the drive encoders
   */
  public void resetEncoders() {
    mLeftMotorOne.getEncoder().setPosition(0.0);
    mLeftMotorTwo.getEncoder().setPosition(0.0);
    mLeftMotorThree.getEncoder().setPosition(0.0);
    
    mRightMotorOne.getEncoder().setPosition(0.0);
    mRightMotorTwo.getEncoder().setPosition(0.0);
    mRightMotorThree.getEncoder().setPosition(0.0);
  }

  
  /**
   * resets alt encoders to 0.0
   */
  public void resetAltEncoders(){
    mAltEncoderLeft.reset();
    mAltEncoderRight.reset();
  }

 /**
  * reset the odometry
  */
  public void resetOdometry(){
    System.out.println("Resetting odometry");
    resetAltEncoders();
    resetEncoders();
    resetGyroCompletely();

    mOdometry.resetPosition(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)), getHeading());
    mInvertedOdometry.resetPosition(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)), getHeading());
  }

  public void resetGyro() {
    // mGyroOffset += mGyro.getAngle();
    mGyro.reset();
  }

  public void resetGyroCompletely(){
    mGyroOffset = 0.0;
    mGyro.reset();
  }
  
  /**
   * gets the double value for the angle of the gyro
   * @return the angle of the gyro
   */
  public double getGyroAngle() {
    return DaisyMath.boundAngleNeg180to180Degrees(mGyro.getAngle() - mGyroOffset);
  }

  /**
   * gets the double value for the pitch of the gyro
   * @return the pitch of the gyro
   */
  public double getGyroPitch() {
    return DaisyMath.boundAngleNeg180to180Degrees(mGyro.getPitch() - mPitchOffset);
  }

  public Pose2d getInvertedPose() {
    return mInvertedOdometry.getPoseMeters();
  }

  /**
   * Gets the differential drive object. This allows us to apply several actions to the drive base from anywhere. 
   * @return Differential drive object
   */
  public DifferentialDrive getDifferentialDrive() {
    return mDifferentialDrive;
  }

  public void logToDashboard() {
    // SmartDashboard.putBoolean("Turn Dampening", getTurnDampening());
    SmartDashboard.putNumber("Drive/Gyro Angle", getGyroAngle());
    SmartDashboard.putNumber("Drive/Gyro Pitch", getGyroPitch());
    SmartDashboard.putNumber("Drive/Left Motors Speed Percent", getLeftDriveSpeedPercent());
    SmartDashboard.putNumber("Drive/Right Motors Speed Percent", getRightDriveSpeedPercent());
    SmartDashboard.putBoolean("Drive/Are Poppers Deployed", getPopperState());
    SmartDashboard.putNumber("Drive/Degree Rotation From Start", getRotationInDegrees());
    SmartDashboard.putNumber("Drive/Translation On X Since Start", getTranslationX());
    SmartDashboard.putNumber("Drive/Translation on Y Since Start", getTranslationY());
    SmartDashboard.putNumber("Drive/Left Wheel Speed in Inches per Second", mAltEncoderLeft.getRate());
    SmartDashboard.putNumber("Drive/Right Wheel Speed in Inches per Second", mAltEncoderRight.getRate());
    SmartDashboard.putNumber("Drive/Left Wheel Distance in Inches", getLeftDistanceInches());
    SmartDashboard.putNumber("Drive/Right Wheel Distance in Inches", getRightDistanceInches());
    SmartDashboard.putNumber("Drive/X Translation", mOdometry.getPoseMeters().getTranslation().getX());
    SmartDashboard.putNumber("Drive/Y Translation", mOdometry.getPoseMeters().getTranslation().getY());
    SmartDashboard.putNumber("Drive/Pose Angle", mOdometry.getPoseMeters().getRotation().getDegrees());
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run

    // TODO extract it to its own command, then call that wherever
    //These methods define movement according to tank and arcade drive systems as allowed for by the differential drive object. 
    // mDifferentialDrive.arcadeDrive(RobotContainer.mDriverController.getY(Hand.kLeft), RobotContainer.mDriverController.getX(Hand.kRight), false);
  
    mOdometry.update(getHeading(), getLeftDistanceMeters(), getRightDistanceMeters());
    mInvertedOdometry.update(getHeading(), -1.0 * getRightDistanceMeters(), -1.0 * getLeftDistanceMeters());

    // mOdometry.update(getHeading(), getDifferentialDriveSpeed());
    // mInvertedOdometry.update(getHeading(), getInvertedDifferentialDriveSpeed());
    logToDashboard();
  }
}
