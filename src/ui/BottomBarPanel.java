package ui;

import javax.swing.JPanel;

public class BottomBarPanel extends JPanel {
    ButtonDesign homeButton = new ButtonDesign("홈");
    ButtonDesign productButton = new ButtonDesign("상품");
    ButtonDesign creditButton = new ButtonDesign("프로그램 정보");

    BottomBarPanel() {
        add(homeButton);
        add(productButton);
        add(creditButton);

        creditButton.addActionListener(e -> WindowBuilder.card.show(WindowBuilder.bankingPane, "프로그램 정보"));
        homeButton.addActionListener(e -> WindowBuilder.card.show(WindowBuilder.bankingPane, "메인화면"));
        productButton.addActionListener(e -> WindowBuilder.card.show(WindowBuilder.bankingPane, "은행상품"));
    }

}
