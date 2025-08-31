package OldTeamCode.TeleOp;//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.config.Config;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.io.File;
import java.io.IOException;
//@Config
@TeleOp(name = "TournamentOpMode V1.1.1 [Updated 1/30/25]")
public class TournamentOpMode extends LinearOpMode

{
    //region: Creating Variables
    private DcMotor WheelMotorLeftFront;
    private DcMotor WheelMotorLeftBack;
    private DcMotor WheelMotorRightBack;
    private DcMotor WheelMotorRightFront;
    private DcMotor extendoLeft;
    private DcMotor extendoRight;
    private DcMotorEx armPivot;
    private Servo clawServo;
    private Servo otherClawServo;
    private boolean hangingMode;
    private Integer armPivotTarget;
    //endregion

    //region: PID Test
    double integralSum = 0;
    //Gradually increase Kp until it is somewhat stable
    double Kp = 0.1;
    //Change after Kp until we get some good shit
    double Ki = 0.01;
    //Change after Kd until we get some even better shit
    double Kd = 0.01;
    double Kf = 0;
    ElapsedTime timer = new ElapsedTime();
    double lastError = 0;
    //endregion

    @Override
    public void runOpMode() throws InterruptedException {
        //region: Initialize Variables
        //These variables do NOT correspond to a physical object; they are entirely digital and for coding purposes.
        hangingMode = false;

        //This section maps the variables to their corresponding motors/servos.
        WheelMotorLeftFront = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorLeftFront", hardwareMap);
        WheelMotorLeftBack = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorLeftBack", hardwareMap);
        WheelMotorRightFront = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorRightFront", hardwareMap);
        WheelMotorRightBack = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorRightBack", hardwareMap);

        //This section sets the directions of motors which should move in reverse so the robot works.
        WheelMotorLeftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        WheelMotorLeftBack.setDirection(DcMotorSimple.Direction.REVERSE);

        //This section initializes the motors that control the extension arms and sets their settings
        extendoLeft = hardwareMap.dcMotor.get("extendoLeft");
        extendoRight = hardwareMap.dcMotor.get("extendoRight");
        extendoRight.setDirection(DcMotorSimple.Direction.REVERSE);
        extendoLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        extendoRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //Use BRAKE zero power behavior so that the motors do not allow the arms to move when no power is applied
        extendoLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        extendoRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

//        armPivot = HelpfulFunctions.MotorFunctions.initializeMotor("armPivot", hardwareMap);
        armPivot = hardwareMap.get(DcMotorEx.class,"armPivot");
//        armPivot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armPivot.setDirection(DcMotorSimple.Direction.FORWARD);
//        armPivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        armPivot.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        armPivot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armPivot.setTargetPositionTolerance(3);
        armPivotTarget = 0;
//        armPivot.setTargetPositionTolerance(8);

        //This section initializes the claw servo
        otherClawServo = hardwareMap.servo.get("otherClawServo");
        clawServo = hardwareMap.servo.get("clawServo");
        //endregion

        //region: Initialize the IMU for navigation
        // Retrieve the IMU from the hardware map
        IMU imu = hardwareMap.get(IMU.class, "imu");
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imu.initialize(parameters);
        //endregion

        //Wait for the user to hit start
        waitForStart();
        //Called continuously while OpMode is active
        while(opModeIsActive()) {



//            telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

            //region: PID code
//            armPivot.
//            double power = PIDControl(1000, armPivot.getVelocity()) / 10;
            //endregion

            // This button choice was made so that it is hard to hit on accident.
            // This will reset the robot's navigation
            if (gamepad1.options) {
                imu.resetYaw();
            }

            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = -gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

            //Allow the users to move at half speed if they are holding A
            if (gamepad1.a) {
                //Apply half the power
                denominator = denominator*2;
            }

            double WheelMotorFrontLeftPower = (y + x + rx) / denominator;
            double WheelMotorBackLeftPower = (y - x + rx) / denominator;
            double WheelMotorFrontRightPower = (y - x - rx) / denominator;
            double WheelMotorBackRightPower = (y + x - rx) / denominator;
//

            //Apply the power
            WheelMotorLeftFront.setPower(WheelMotorFrontLeftPower);
            WheelMotorLeftBack.setPower(WheelMotorBackLeftPower);
            WheelMotorRightFront.setPower(WheelMotorFrontRightPower);
            WheelMotorRightBack.setPower(WheelMotorBackRightPower);
            //endregion

            //region: ArmPivot Controls
            if(gamepad2.triangle && !(extendoLeft.getCurrentPosition() < -1500)) {
                armPivot.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                armPivot.setPower(0.75);
            }
            else if (gamepad2.cross) {
                armPivot.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                armPivot.setPower(-0.75);
            }
            else if (gamepad2.right_stick_button) {
                armPivot.setTargetPosition(180);
                armPivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                armPivot.setPower(0.75);
            }
            else {
                armPivot.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                armPivot.setPower(0);
            }
//            if(gamepad2.b) {
//                armPivotTarget = 135;
//            }
//            else if(gamepad2.y) {
//                armPivotTarget = 70;
//            }
//            else if(gamepad2.x) {
//                armPivotTarget = 20;
//            }
//
//
//            if(armPivot.getCurrentPosition() > armPivotTarget) {
//                armPivot.setPower(-0.5);
//            }
//            else if(armPivot.getCurrentPosition() < armPivotTarget) {
//                armPivot.setPower(0.5);
//            }
//            else {
//                armPivot.setPower(0);
//            }


//            if(gamepad2.b) {
//                if(armPivot.getCurrentPosition() < 120) {
//                    armPivot.setPower(-1);
//                }
//                else {
//                    armPivot.setPower(1);
//                }
//                armPivot.setTargetPosition(120);
//                armPivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            }
//            else if(gamepad2.y) {
//                if(armPivot.getCurrentPosition() > 80) {
//                    armPivot.setPower(-1);
//                }
//                else {
//                    armPivot.setPower(1);
//                }
//                armPivot.setTargetPosition(80);
//                armPivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            }
//            else if(gamepad2.x) {
//                if(armPivot.getCurrentPosition() > 0) {
//                    armPivot.setPower(-1);
//                }
//                else {
//                    armPivot.setPower(1);
//                }
//                armPivot.setTargetPosition(20);
//                armPivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            }
//            if(gamepad2.dpad_up) {
//                armPivot.setPower(1);
//            }
//            else if (gamepad2.dpad_down) {
//                armPivot.setPower(-1);
//            }
//            else if(gamepad2.a){
//                armPivot.setTargetPosition(armPivot.getCurrentPosition());
//                armPivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                armPivot.setPower(1);
//            }
//            else {
//                armPivot.setPower(0);
//            }
            //endregion

            //region: Extendo arm controls
            if(gamepad2.dpad_up && !(armPivot.getCurrentPosition() > 160)) {
                extendoRight.setPower(1);
                extendoLeft.setPower(1);
            }
            else if(gamepad2.dpad_down) {
                extendoLeft.setPower(-1);
                extendoRight.setPower(-1);
            }
            else if(gamepad2.circle && gamepad2.share) {
                //Make the robot start or stop hanging by setting hangingMode to true or false
                if(!hangingMode) {
                    hangingMode = true;
                }

            }
            else if(hangingMode) {
                //If the robot has entered hanging mode, keep it hanging!
                extendoLeft.setPower(-1);
                extendoRight.setPower(-1);
            }
            else if (gamepad2.square) {
                hangingMode = false;
            }
            else {
                //If the robot is not hanging and nothing is being pressed, apply no power to the arm
                extendoLeft.setPower(0);
                extendoRight.setPower(0);
            }
            //endregion

            //region: Claw controls
            if(gamepad2.right_bumper) {
                clawServo.setPosition(clawServo.getPosition() + 0.05);
                otherClawServo.setPosition(otherClawServo.getPosition() - 0.05);
            }
            else if(gamepad2.left_bumper) {
                clawServo.setPosition(clawServo.getPosition() - 0.05);
                otherClawServo.setPosition(otherClawServo.getPosition() + 0.05);
            }
            else if(gamepad2.right_trigger > 0) {
                clawServo.setPosition(clawServo.getPosition() + 0.05);
            }
            else if (gamepad2.left_trigger > 0) {
                clawServo.setPosition(clawServo.getPosition() - 0.05);
            }
            //endregion

            telemetry.addData("Left Extendo", extendoLeft.getCurrentPosition());
            telemetry.addData("Right Extendo", extendoRight.getCurrentPosition());
            telemetry.addData("Arm position", armPivot.getCurrentPosition());
            telemetry.addData("Claw Position", clawServo.getPosition());
            telemetry.update();
        }
    }

    public double PIDControl(double reference, double    state) {
        double error = reference - state;
        integralSum += error * timer.seconds();
        double derivative = (error - lastError) / timer.seconds();
        lastError = error;

        telemetry.addData("error: ", error);
        telemetry.addData("integralSum: ", integralSum);
        telemetry.addData("derivative", derivative);

        double output = (error * Kp) + (derivative * Kd) + (integralSum * Ki) + (reference * Kf);
        telemetry.addData("output", output);
        return output;

    }
//    public double maintainArmPivotPosition() {
//        float vertex = 100;
//        float difference = vertex - armPivot.getCurrentPosition();
//        telemetry.addData("diff:", difference);
//        if (difference==0) {
//            telemetry.addData("aaaaaaaaaa", "aaaa");
//            return 0;
//        }
//        double diffSquared = Math.pow(difference, 2);
//        double factor = Math.abs(diffSquared/100);
//        telemetry.addData("factor: ", factor);
//        return factor;
//    }
}