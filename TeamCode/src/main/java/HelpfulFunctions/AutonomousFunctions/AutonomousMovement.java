package HelpfulFunctions.AutonomousFunctions;

import com.qualcomm.robotcore.hardware.DcMotor;

abstract public class AutonomousMovement {

    //These stats are for the gobuilda mecanum wheels currently attached to the robot at the end of the 2024-2025 season:
    public static float wheelCircumference = 11.8737f;
    public static float wheelSideLength = 1.486f;
    public static float ticksPerRevolution = 537.7f;

    public static int moveDistanceInInchesRight(float distance, DcMotor WheelMotorLeftFront, DcMotor WheelMotorLeftBack, DcMotor WheelMotorRightBack, DcMotor WheelMotorRightFront) {
        /*
        Moves a float distance of inches right. Use negative values for left.
         */
        DcMotor[] WheelMotors = new DcMotor[4];
        WheelMotors[0] = WheelMotorLeftFront;
        WheelMotors[1] = WheelMotorRightFront;
        WheelMotors[2] = WheelMotorLeftBack;
        WheelMotors[3] = WheelMotorRightBack;

        int magnitude = Math.round((distance/wheelSideLength) * (ticksPerRevolution * (wheelSideLength/wheelCircumference)));
        WheelMotorLeftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorLeftFront.setTargetPosition(magnitude);
        WheelMotorLeftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        int target = magnitude;

        WheelMotorLeftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorLeftBack.setTargetPosition(-magnitude);
        WheelMotorLeftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        WheelMotorRightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorRightFront.setTargetPosition(-magnitude);
        WheelMotorRightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        WheelMotorRightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorRightBack.setTargetPosition(magnitude);
        WheelMotorRightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        for(DcMotor motor : WheelMotors) {
            motor.setPower(0.25);
        }
        return target;
    }
    public static int moveDistanceInInches(float distance, DcMotor WheelMotorLeftFront, DcMotor WheelMotorLeftBack, DcMotor WheelMotorRightBack, DcMotor WheelMotorRightFront) {
        /*
        Moves distance inches forward. Use negative values for backward.
         */

        DcMotor[] WheelMotors = new DcMotor[4];
        WheelMotors[0] = WheelMotorLeftFront;
        WheelMotors[1] = WheelMotorRightFront;
        WheelMotors[2] = WheelMotorLeftBack;
        WheelMotors[3] = WheelMotorRightBack;

        for(DcMotor motor : WheelMotors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setTargetPosition(-Math.round((distance/wheelCircumference) * ticksPerRevolution));
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }

        int target;
        target = -Math.round((distance/wheelCircumference) * ticksPerRevolution);

        for(DcMotor motor: WheelMotors) {
            motor.setPower(0.5);
        }

        return target;
    }
    public static int rotateInDegrees(float degrees, DcMotor WheelMotorLeftFront, DcMotor WheelMotorLeftBack, DcMotor WheelMotorRightBack, DcMotor WheelMotorRightFront) {
        /*
        Rotates in place n degrees clockwise. Use negative values for counterclockwise.
         */
        int magnitude = (int) Math.round(4600 * Math.sin((Math.PI/180) * degrees/6.325));
        WheelMotorLeftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorLeftFront.setTargetPosition(magnitude);
        WheelMotorLeftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        int target = magnitude;

        WheelMotorLeftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorLeftBack.setTargetPosition(magnitude);
        WheelMotorLeftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        WheelMotorRightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorRightFront.setTargetPosition(-magnitude);
        WheelMotorRightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        WheelMotorRightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorRightBack.setTargetPosition(-magnitude);
        WheelMotorRightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        WheelMotorRightBack.setPower(0.25);
        WheelMotorRightFront.setPower(0.25);
        WheelMotorLeftBack.setPower(0.25);
        WheelMotorLeftFront.setPower(0.25);

        return target;
    }
    public static int moveDirectionInInches(String direction, float distance, DcMotor WheelMotorLeftFront, DcMotor WheelMotorLeftBack, DcMotor WheelMotorRightBack, DcMotor WheelMotorRightFront) {
        if(direction == "right") {
            return moveDistanceInInchesRight(distance, WheelMotorLeftFront, WheelMotorLeftBack, WheelMotorRightBack, WheelMotorRightFront);
        }
        if(direction == "left") {
            return moveDistanceInInchesRight(-distance, WheelMotorLeftFront, WheelMotorLeftBack, WheelMotorRightBack, WheelMotorRightFront);
        }
        if(direction == "forward") {
            return moveDistanceInInches(distance, WheelMotorLeftFront, WheelMotorLeftBack, WheelMotorRightBack, WheelMotorRightFront);
        }
        if(direction == "back") {
            return moveDistanceInInches(-distance, WheelMotorLeftFront, WheelMotorLeftBack, WheelMotorRightBack, WheelMotorRightFront);
        }
        else {
            return Integer.MAX_VALUE;
        }
    }
    public static void moveRobotInDirection(String direction, float speedMultipler, DcMotor WheelMotorLeftFront, DcMotor WheelMotorLeftBack, DcMotor WheelMotorRightBack, DcMotor WheelMotorRightFront) {
        if (direction == "forward") {
            WheelMotorLeftFront.setPower(-1 * speedMultipler);
            WheelMotorRightFront.setPower(1 * speedMultipler);
            WheelMotorLeftBack.setPower(-1 * speedMultipler);
            WheelMotorRightBack.setPower(1 * speedMultipler);
        } else if (direction == "backward") {
            WheelMotorLeftFront.setPower(1 * speedMultipler);
            WheelMotorRightFront.setPower(-1 * speedMultipler);
            WheelMotorLeftBack.setPower(1 * speedMultipler);
            WheelMotorRightBack.setPower(-1 * speedMultipler);
        } else if (direction == "left") {
            WheelMotorLeftFront.setPower(1 * speedMultipler);
            WheelMotorRightFront.setPower(1 * speedMultipler);
            WheelMotorLeftBack.setPower(1 * speedMultipler);
            WheelMotorRightBack.setPower(1 * speedMultipler);
        } else if (direction == "right") {
            WheelMotorLeftFront.setPower(-1 * speedMultipler);
            WheelMotorRightFront.setPower(-1 * speedMultipler);
            WheelMotorLeftBack.setPower(-1 * speedMultipler);
            WheelMotorRightBack.setPower(-1 * speedMultipler);
        }
    }
    public static void stopRobot(DcMotor WheelMotorLeftFront, DcMotor WheelMotorLeftBack, DcMotor WheelMotorRightBack, DcMotor WheelMotorRightFront) {
        WheelMotorLeftFront.setPower(0);
        WheelMotorRightFront.setPower(0);
        WheelMotorLeftBack.setPower(0);
        WheelMotorRightBack.setPower(0);
    }

}
