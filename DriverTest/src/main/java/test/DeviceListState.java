package test;

public class DeviceListState {
    public boolean isOpenDisable = false;
    public boolean isClaimDisable = false;
    public boolean isReleaseDisable = false;
    public boolean isCloseDisable = false;
    public boolean isScannedTextFiledDisable = false;
    public DeviceListState() {
        isOpenDisable = false;
        isClaimDisable = true;
        isReleaseDisable = true;
        isCloseDisable = true;
        isScannedTextFiledDisable = true;
    }
    void onOpenClicked() {
        isOpenDisable = true;
        isClaimDisable = false;
        isReleaseDisable = true;
        isCloseDisable = false;
        isScannedTextFiledDisable = false;
    }
    void onClaimClicked() {
        isOpenDisable = true;
        isClaimDisable = true;
        isReleaseDisable = false;
        isCloseDisable = false;
        isScannedTextFiledDisable = false;
    }
    void onReleaseClicked() {
        isOpenDisable = true;
        isClaimDisable = false;
        isReleaseDisable = true;
        isCloseDisable = false;
        isScannedTextFiledDisable = false;
    }
    void onCloseClicked() {
        isOpenDisable = false;
        isClaimDisable = true;
        isReleaseDisable = true;
        isCloseDisable = true;
        isScannedTextFiledDisable = true;
    }
}
