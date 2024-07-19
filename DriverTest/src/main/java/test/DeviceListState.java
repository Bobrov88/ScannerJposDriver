package test;

public class DeviceListState {
    public boolean isOpenDisable = false;
    public boolean isClaimDisable = false;
    public boolean isReleaseDisable = false;
    public boolean isCloseDisable = false;
    public boolean isScannedTextAreaDisable = false;
    public boolean isShowDeviceInfoDisable = false;
    public boolean isCopyScannedDisable = false;
    public boolean isClearDisable = false;
    public DeviceListState() {
        isOpenDisable = false;
        isClaimDisable = true;
        isReleaseDisable = true;
        isCloseDisable = true;
        isScannedTextAreaDisable = true;
        isShowDeviceInfoDisable = true;
        isCopyScannedDisable = true;
        isClearDisable = true;
    }
    void onOpenClicked() {
        isOpenDisable = true;
        isClaimDisable = false;
        isReleaseDisable = true;
        isCloseDisable = false;
        isScannedTextAreaDisable = false;
        isShowDeviceInfoDisable = false;
        isCopyScannedDisable = false;
        isClearDisable = false;
    }
    void onClaimClicked() {
        isOpenDisable = true;
        isClaimDisable = true;
        isReleaseDisable = false;
        isCloseDisable = false;
        isScannedTextAreaDisable = false;
        isShowDeviceInfoDisable = false;
        isCopyScannedDisable = false;
        isClearDisable = false;
    }
    void onReleaseClicked() {
        isOpenDisable = true;
        isClaimDisable = false;
        isReleaseDisable = true;
        isCloseDisable = false;
        isScannedTextAreaDisable = false;
        isShowDeviceInfoDisable = false;
        isCopyScannedDisable = false;
        isClearDisable = false;
    }
    void onCloseClicked() {
        isOpenDisable = false;
        isClaimDisable = true;
        isReleaseDisable = true;
        isCloseDisable = true;
        isScannedTextAreaDisable = true;
        isShowDeviceInfoDisable = true;
        isCopyScannedDisable = true;
        isClearDisable = true;
    }
}