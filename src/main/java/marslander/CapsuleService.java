package marslander;

public class CapsuleService {
    private boolean positiveRotate = true;
    private int power = 0;
    private int rotate = 0;
    private final Mars mars;
    private final MarsService marsService;

    public CapsuleService(Mars mars) {
        this.mars = mars;
        this.marsService = new MarsService();
    }

    public int getNextRotate(Capsule capsule) {
        positiveRotate = !positiveRotate;
        rotate = (rotate + 15) % 90;
        return rotate;
    }


    public int getNextPower(Capsule capsule) {
        power = (power + 1) % 5;
        return power;
    }
}
