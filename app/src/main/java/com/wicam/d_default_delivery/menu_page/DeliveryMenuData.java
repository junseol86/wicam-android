package com.wicam.d_default_delivery.menu_page;

/**
 * Created by Hyeonmin on 2015-08-02.
 */
public class DeliveryMenuData {
    private String string, menuName = "", menuDesc = "", menuPrice = "", groupName = "", groupDesc = "", groupPrice = "";
    private int groupOrMenu;

    public DeliveryMenuData(String string) {
        this.string = string;

        if (string.contains("@")) { // �׷��� ��
            groupOrMenu = 0;
            if (string.contains("#")) { // �׷� ������ ���� ��
                String[] groupStrings = string.split("#");
                groupName = groupStrings[0];
                if (groupStrings[1].contains(":")) { // �׷� ���� �ְ� ���ݵ� ���� ��
                    groupDesc = groupStrings[1].split(":")[0];
                    groupPrice = groupStrings[1].split(":")[1];
                }
                else { // �׷� ���� ���� ��
                    groupDesc = groupStrings[1];
                }
            }
            else if (string.contains(":")) { // �׷� ������ ���� ������ ���� ��
                groupName = string.split(":")[0];
                groupPrice = string.split(":")[1];
            }
            else { // �׷�� ���� ��
                groupName = string;
            }
            groupName = groupName.replace("@", "");

        }
        else { // �޴��� ��

            groupOrMenu = 1;
            if (string.contains("#")) { // �޴� ������ ���� ��
                String[] menuStrings = string.split("#");
                menuName = menuStrings[0];
                if (menuStrings[1].contains(":")) { // �޴� ���� �ְ� ���ݵ� ���� ��
                    menuDesc = menuStrings[1].split(":")[0];
                    menuPrice = menuStrings[1].split(":")[1];
                }
                else { // �޴� ���� ���� ��
                    menuDesc = menuStrings[1];
                }
            }
            else if (string.contains(":")) { // �޴� ������ ���� ������ ���� ��
                menuName = string.split(":")[0];
                menuPrice = string.split(":")[1];
            }
            else { // �޴��� ���� ��
                menuName = string;
            }

        }
    }

    public String getString() {
        return string;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getMenuDesc() {
        return menuDesc;
    }

    public String getMenuPrice() {
        return menuPrice;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public String getGroupPrice() {
        return groupPrice;
    }

    public int getGroupOrMenu() {
        return groupOrMenu;
    }
}
