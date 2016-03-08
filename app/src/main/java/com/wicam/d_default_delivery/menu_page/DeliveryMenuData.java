package com.wicam.d_default_delivery.menu_page;

/**
 * Created by Hyeonmin on 2015-08-02.
 */
public class DeliveryMenuData {
    private String string, menuName = "", menuDesc = "", menuPrice = "", groupName = "", groupDesc = "", groupPrice = "";
    private int groupOrMenu;

    public DeliveryMenuData(String string) {
        this.string = string;

        if (string.contains("@")) { // 그룹일 시
            groupOrMenu = 0;
            if (string.contains("#")) { // 그룹 설명이 있을 시
                String[] groupStrings = string.split("#");
                groupName = groupStrings[0];
                if (groupStrings[1].contains(":")) { // 그룹 설명도 있고 가격도 있을 시
                    groupDesc = groupStrings[1].split(":")[0];
                    groupPrice = groupStrings[1].split(":")[1];
                }
                else { // 그룹 설명만 있을 시
                    groupDesc = groupStrings[1];
                }
            }
            else if (string.contains(":")) { // 그룹 설명은 없고 가격은 있을 시
                groupName = string.split(":")[0];
                groupPrice = string.split(":")[1];
            }
            else { // 그룹명만 있을 시
                groupName = string;
            }
            groupName = groupName.replace("@", "");

        }
        else { // 메뉴일 시

            groupOrMenu = 1;
            if (string.contains("#")) { // 메뉴 설명이 있을 시
                String[] menuStrings = string.split("#");
                menuName = menuStrings[0];
                if (menuStrings[1].contains(":")) { // 메뉴 설명도 있고 가격도 있을 시
                    menuDesc = menuStrings[1].split(":")[0];
                    menuPrice = menuStrings[1].split(":")[1];
                }
                else { // 메뉴 설명만 있을 시
                    menuDesc = menuStrings[1];
                }
            }
            else if (string.contains(":")) { // 메뉴 설명은 없고 가격은 있을 시
                menuName = string.split(":")[0];
                menuPrice = string.split(":")[1];
            }
            else { // 메뉴명만 있을 시
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
