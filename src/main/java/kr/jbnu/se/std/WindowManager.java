package kr.jbnu.se.std;

public class WindowManager {
    private LoginClient loginClient;
    private MainClient mainClient;
    private InviteFriends inviteFriends;
    private ShopWindow shopWindow;
    private InventoryWindow inventoryWindow;
    private RankWindow rankWindow;
    private Framework framework;

    public WindowManager(Framework framework) {
        loginClient = new LoginClient(framework);
        this.framework = framework;
    }

    public void openLoginWindow() {
        loginClient.setVisible(true);
    }

    public void openMainWindow() {
        mainClient = new MainClient(framework);
        mainClient.setVisible(true);
    }

    public void openInviteFriendsWindow() {
        inviteFriends = new InviteFriends(framework);
        inviteFriends.setVisible(true);
    }

    public void openShopWindow() {
        shopWindow = new ShopWindow(framework);
        shopWindow.setVisible(true);
    }

    public void openInventoryWindow() {
        inventoryWindow = new InventoryWindow(framework);
        inventoryWindow.setVisible(true);
    }

    public void openRankWindow() {
        rankWindow = new RankWindow();
        rankWindow.setVisible(true);
    }

    public void closeAllWindows() {
        if (loginClient != null) loginClient.dispose();
        if (mainClient != null) mainClient.dispose();
        if (inviteFriends != null) inviteFriends.dispose();
        if (shopWindow != null) shopWindow.dispose();
        if (inventoryWindow != null) inventoryWindow.dispose();
        if (rankWindow != null) rankWindow.dispose();
    }

    public MainClient getMainWindow() {
        return mainClient;
    }

    public InventoryWindow getInventoryWindow() {
        return inventoryWindow;
    }
}
