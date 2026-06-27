package caro.view;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.nio.file.Paths;

import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.ButtonGroup;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import caro.bean.RoundedBorder;
import caro.bean.Setting;
import caro.bo.CaroAI;
import caro.bo.ai.AIConfig;
import caro.dao.SettingDao;
import caro.values.Value;

public class App extends JFrame implements MouseListener{

    private JPanel contentPane;
    private JPanel TableCells; // Panel chứa ma trận cells
    private Border cellBorder; // tạo đường viền của mỗi cell
    private JLabel[][] cell; // Ma trận cells
    private JLabel userClickedCell; // cell được user click chọn
    private JLabel aiClickedCell; // cell được AI click chọn
    private JLabel lblUserScore; // điểm của User
    private JLabel lblAIScore; // điểm của AI
    private JLabel lblCurrentAIModel; // tên mô hình AI đang chọn

    private CaroAI caro;
    private Setting setting;
    private Notification notification;

    public static final int TEXT_CELL_SIZE = Value.TEXT_CELL_SIZE; // cỡ chữ trong mỗi cell

    private String currentPath; // đường dẫn hiện tại của project

    private final ButtonGroup buttonGroup = new ButtonGroup();

    private static final String UI_FONT = "Tahoma";
    private static final Font FONT_TITLE = new Font(UI_FONT, Font.BOLD, 36);
    private static final Font FONT_SECTION = new Font(UI_FONT, Font.BOLD, 16);
    private static final Font FONT_BUTTON = new Font(UI_FONT, Font.BOLD, 12);
    private static final Font FONT_RADIO = new Font(UI_FONT, Font.PLAIN, 14);
    private static final Font FONT_SCORE = new Font(UI_FONT, Font.BOLD, 11);
    private static final Font FONT_CELL = new Font(UI_FONT, Font.BOLD, Value.TEXT_CELL_SIZE);
    private static final Color COLOR_BTN_FG = new Color(85, 107, 47);
    private static final Color COLOR_BTN_BG = new Color(255, 20, 147);
    private static final Color COLOR_SECTION = new Color(0, 0, 139);
    private static final Color COLOR_RADIO = new Color(107, 142, 35);

    private void styleSidebarButton(JButton button) {
        button.setOpaque(false);
        button.setForeground(COLOR_BTN_FG);
        button.setFont(FONT_BUTTON);
        button.setBorder(new RoundedBorder(10));
        button.setBackground(COLOR_BTN_BG);
        button.setFocusPainted(false);
    }


     //Launch the application.

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    App frame = new App();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


     // Tạo game mới, clear màn chơi cũ

    public void newGame() {
        newGame(true);
    }

    private void newGame(boolean reloadSetting) {
        if (reloadSetting) {
            setting = SettingDao.LoadSettingInfo();
        }
        caro = new CaroAI(setting.getMode(), setting.getAiConfig());
        updateAIModelLabel();
        userClickedCell = null;
        aiClickedCell = null;
        for (int i = 0; i < Value.SIZE; i++) {
            for (int j = 0; j < Value.SIZE; j++) {
                cell[i][j].setBackground(setting.getCellColor());
                cell[i][j].setForeground(setting.getxColor());
                cell[i][j].setText("");
            }
        }
        if(setting.getMode() == 1) {
            // cập nhật nước đi của AI
            int x = caro.getNextX();
            int y = caro.getNextY();
            updateTableCells(x, y, Value.AI_VALUE);
        }
    }

    public void updateTableCells(int x, int y, int player) {
        if(player == Value.AI_VALUE) {
            if(aiClickedCell != null) {
                aiClickedCell.setBackground(setting.getCellColor()); // đặt lại màu clickedCell cũ
            }
            aiClickedCell = cell[x][y];
            aiClickedCell.setForeground(setting.getoColor());
            aiClickedCell.setText("O");
            aiClickedCell.setBackground(Value.CLICK_CELL_COLOR); // làm nổi bật cell được AI chọn
        }
        else {
            cell[x][y].setBackground(setting.getCellColor()); // đặt lại màu clickedCell cũ
            cell[x][y].setText("X");
        }
    }

    public Notification getNotificationInstance() {
        if(notification == null) notification = new Notification();
        return notification;
    }


     //Create the frame.
    public App() {
        //Set các giá trị mặc định
        setResizable(false);
        currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        setIconImage(Toolkit.getDefaultToolkit().getImage(currentPath+"\\file\\img\\icon.png"));
        setTitle("Cờ Caro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Value.SIZE*Value.CELL_WIDTH+3*Value.MARGIN+280, Value.SIZE*Value.CELL_WIDTH+50);//kích thước của mỗi phần thay đổi tự động khi thay đổi SIZE(số hàng/số cột)
        setLocationRelativeTo(null);


        setting = SettingDao.LoadSettingInfo();
        caro = new CaroAI(setting.getMode(), setting.getAiConfig()); // khởi tạo CaroAI
        cellBorder = new LineBorder(Color.black, 1); // tạo border cho mỗi cell trong ma trận

        /*------------------Tạo các đối tượng------------------*/
        contentPane = new JPanel();
        contentPane.setBackground(setting.getBackgroundColor());
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        TableCells = new JPanel();
        TableCells.setBackground(new Color(255, 255, 255));
        TableCells.setLayout(new GridLayout(Value.SIZE, Value.SIZE, 0, 0));
        TableCells.setFont(new Font("Tahoma", Font.PLAIN, 14));
        TableCells.setBounds(Value.MARGIN, Value.MARGIN, Value.SIZE*Value.CELL_WIDTH, Value.SIZE*Value.CELL_WIDTH);
        contentPane.add(TableCells);

        // Tạo ma trận và add vào TableCells
        cell = new JLabel[Value.SIZE][Value.SIZE];
        for (int i = 0; i < Value.SIZE; i++) {
            for (int j = 0; j < Value.SIZE; j++) {
                cell[i][j] = new JLabel();
                cell[i][j].setSize(Value.CELL_WIDTH, Value.CELL_WIDTH); // kích cỡ mỗi cell
                cell[i][j].setOpaque(true);
                cell[i][j].setBorder(cellBorder);
                cell[i][j].setFont(FONT_CELL);
                cell[i][j].setBackground(setting.getCellColor());
                cell[i][j].setForeground(setting.getxColor());
                cell[i][j].setHorizontalAlignment(SwingConstants.CENTER); // căn giữa chữ
                cell[i][j].addMouseListener(this); // add hàm bắt sự kiện click chuột
                TableCells.add(cell[i][j]); // add cell vào TableCells
            }
        }
        // Nếu chế độ AI đánh trước => cập nhật lượt đầu của AI
        if(setting.getMode() == 1) updateTableCells(caro.getNextX(), caro.getNextX(), Value.AI_VALUE);

        JPanel view = new JPanel();
        view.setBackground(new Color(250, 235, 215));
        view.setForeground(Color.BLACK);
        view.setBounds(Value.SIZE*Value.CELL_WIDTH+2*Value.MARGIN, Value.MARGIN, 274, Value.SIZE*Value.CELL_WIDTH);
        contentPane.add(view);
        view.setLayout(null);

        JLabel lbltitle = new JLabel("GAME CARO");
        lbltitle.setHorizontalAlignment(SwingConstants.CENTER);
        lbltitle.setFont(FONT_TITLE);
        lbltitle.setBounds(10, 11, 254, 50);
        view.add(lbltitle);

        JLabel lblMode = new JLabel("Mode:");
        lblMode.setHorizontalAlignment(SwingConstants.LEFT);
        lblMode.setForeground(COLOR_SECTION);
        lblMode.setFont(FONT_SECTION);
        lblMode.setBounds(10, 162, 254, 20);
        view.add(lblMode);

        JButton btnNewGame = new JButton("New Game");
        btnNewGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(null, "Bạn có muốn chơi mới?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if(result == JOptionPane.YES_OPTION) newGame();
            }
        });
        btnNewGame.setBounds(30, 448, 89, 37);
        styleSidebarButton(btnNewGame);
        view.add(btnNewGame);

        JButton btnExitGame = new JButton("Exit Game");
        btnExitGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(null, "Bạn có muốn đóng trò chơi?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if(result == JOptionPane.YES_OPTION) System.exit(0); // thoát game
            }
        });
        btnExitGame.setBounds(156, 448, 89, 37);
        styleSidebarButton(btnExitGame);
        view.add(btnExitGame);

        JRadioButton rdbtnUserPlaysFirst = new JRadioButton("User plays first");
        JRadioButton rdbtnAiPlaysFirst = new JRadioButton("AI plays first");
        if(setting.getMode() == 0) rdbtnUserPlaysFirst.setSelected(true);
        else rdbtnAiPlaysFirst.setSelected(true);

        rdbtnUserPlaysFirst.setForeground(COLOR_RADIO);
        rdbtnUserPlaysFirst.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(setting.getMode() == 1) {
                    int result = JOptionPane.showConfirmDialog(null, "Xác nhận đổi chế độ chơi?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                    if(result == JOptionPane.YES_OPTION) {
                        setting.setMode(0); // cập nhật mode
                        newGame(); // clear màn chơi cũ
                    }
                    else {
                        rdbtnUserPlaysFirst.setSelected(false);
                        rdbtnAiPlaysFirst.setSelected(true);
                    }
                }
            }
        });
        rdbtnUserPlaysFirst.setFont(FONT_RADIO);
        buttonGroup.add(rdbtnUserPlaysFirst);
        rdbtnUserPlaysFirst.setOpaque(false);
        rdbtnUserPlaysFirst.setBounds(26, 192, 232, 23);
        view.add(rdbtnUserPlaysFirst);

        rdbtnAiPlaysFirst.setForeground(COLOR_RADIO);
        rdbtnAiPlaysFirst.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(setting.getMode() == 0) {
                    int result = JOptionPane.showConfirmDialog(null, "Xác nhận đổi chế độ chơi?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                    if(result == JOptionPane.YES_OPTION) {
                        setting.setMode(1); // cập nhật mode
                        newGame(); // clear màn chơi cũ
                    }
                    else {
                        rdbtnUserPlaysFirst.setSelected(true);
                        rdbtnAiPlaysFirst.setSelected(false);
                    }
                }
            }
        });
        rdbtnAiPlaysFirst.setFont(FONT_RADIO);
        buttonGroup.add(rdbtnAiPlaysFirst);
        rdbtnAiPlaysFirst.setOpaque(false);
        rdbtnAiPlaysFirst.setBounds(26, 218, 232, 23);
        view.add(rdbtnAiPlaysFirst);

        JLabel lblAIModel = new JLabel("AI Model:");
        lblAIModel.setHorizontalAlignment(SwingConstants.LEFT);
        lblAIModel.setForeground(COLOR_SECTION);
        lblAIModel.setFont(FONT_SECTION);
        lblAIModel.setBounds(10, 244, 85, 20);
        view.add(lblAIModel);

        lblCurrentAIModel = new JLabel(setting.getAiModelDisplayName());
        lblCurrentAIModel.setHorizontalAlignment(SwingConstants.LEFT);
        lblCurrentAIModel.setForeground(COLOR_RADIO);
        lblCurrentAIModel.setFont(FONT_RADIO);
        lblCurrentAIModel.setBounds(95, 246, 165, 20);
        view.add(lblCurrentAIModel);

        JButton btnSelectAIModel = new JButton("Chọn mô hình AI");
        btnSelectAIModel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAIModelDialog();
            }
        });
        btnSelectAIModel.setBounds(30, 268, 215, 28);
        styleSidebarButton(btnSelectAIModel);
        view.add(btnSelectAIModel);

        JLabel lblSetting = new JLabel("Color:");
        lblSetting.setHorizontalAlignment(SwingConstants.LEFT);
        lblSetting.setForeground(COLOR_SECTION);
        lblSetting.setFont(FONT_SECTION);
        lblSetting.setBounds(10, 312, 254, 20);
        view.add(lblSetting);

        JSeparator separator = new JSeparator();
        separator.setBackground(Color.GRAY);
        separator.setForeground(Color.DARK_GRAY);
        separator.setBounds(10, 427, 254, 2);
        view.add(separator);

        JSeparator separator_1 = new JSeparator();
        separator_1.setForeground(Color.DARK_GRAY);
        separator_1.setBackground(Color.GRAY);
        separator_1.setBounds(10, 149, 254, 2);
        view.add(separator_1);

        JLabel lblUser = new JLabel("USER");
        lblUser.setForeground(new Color(220, 20, 60));
        lblUser.setFont(FONT_SECTION);
        lblUser.setHorizontalAlignment(SwingConstants.CENTER);
        lblUser.setBounds(10, 91, 122, 20);
        view.add(lblUser);

        JLabel lblAI = new JLabel("AI");
        lblAI.setForeground(new Color(0, 139, 139));
        lblAI.setFont(FONT_SECTION);
        lblAI.setHorizontalAlignment(SwingConstants.CENTER);
        lblAI.setBounds(142, 91, 122, 20);
        view.add(lblAI);

        lblUserScore = new JLabel("0");
        lblUserScore.setForeground(new Color(65, 105, 225));
        lblUserScore.setFont(FONT_SCORE);
        lblUserScore.setHorizontalAlignment(SwingConstants.CENTER);
        lblUserScore.setBounds(10, 122, 122, 20);
        view.add(lblUserScore);

        lblAIScore = new JLabel("0");
        lblAIScore.setForeground(new Color(0, 128, 0));
        lblAIScore.setFont(FONT_SCORE);
        lblAIScore.setHorizontalAlignment(SwingConstants.CENTER);
        lblAIScore.setBounds(142, 122, 122, 20);
        view.add(lblAIScore);

        JButton btnInfo = new JButton("Info");
        btnInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                notification = getNotificationInstance();
                notification.show("Thông tin", "Thông Tin", Value.INFO_MESSAGE);
            }
        });
        btnInfo.setBounds(156, 496, 89, 37);
        styleSidebarButton(btnInfo);
        view.add(btnInfo);

        JButton btnIntroduce = new JButton("Introduce");
        btnIntroduce.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                notification = getNotificationInstance();
                notification.show("Thông tin", "Giới Thiệu", Value.INTRODUCE_MESSAGE);
            }
        });
        btnIntroduce.setBounds(30, 496, 89, 37);
        styleSidebarButton(btnIntroduce);
        view.add(btnIntroduce);

        JButton btnXColor = new JButton("Màu X");
        btnXColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Color color = JColorChooser.showDialog(App.this,
                        "Chọn màu chữ của User", setting.getxColor());
                if(color != null) {
                    setting.setxColor(color);
                    newGame();
                }
            }
        });
        btnXColor.setBounds(30, 347, 89, 28);
        styleSidebarButton(btnXColor);
        view.add(btnXColor);

        JButton btnBackgroundColor = new JButton("Màu Nền");
        btnBackgroundColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(App.this,
                        "Chọn màu nền", setting.getBackgroundColor());
                if(color != null) {
                    setting.setBackgroundColor(color);
                    contentPane.setBackground(color);
                }
            }
        });
        btnBackgroundColor.setBounds(30, 386, 89, 28);
        styleSidebarButton(btnBackgroundColor);
        view.add(btnBackgroundColor);

        JButton btnOColor = new JButton("Màu O");
        btnOColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(App.this,
                        "Chọn màu chữ của AI", setting.getoColor());
                if(color != null) {
                    setting.setoColor(color);
                    newGame();
                }
            }
        });
        btnOColor.setBounds(156, 347, 89, 28);
        styleSidebarButton(btnOColor);
        view.add(btnOColor);

        JButton btnCellColor = new JButton("Màu Ô");
        btnCellColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(App.this,
                        "Chọn màu của mỗi ô vuông", setting.getCellColor());
                if(color != null) {
                    setting.setCellColor(color);
                    newGame();
                }
            }
        });
        btnCellColor.setBounds(156, 386, 89, 28);
        styleSidebarButton(btnCellColor);
        view.add(btnCellColor);


    }

    private void updateAIModelLabel() {
        if (lblCurrentAIModel != null && setting != null) {
            lblCurrentAIModel.setText(setting.getAiModelDisplayName());
        }
    }

    private void showAIModelDialog() {
        AIConfig current = setting.getAiConfig();
        AIConfig selected = (AIConfig) JOptionPane.showInputDialog(
                this,
                "Chọn mô hình AI để chơi cùng:",
                "Cài đặt mô hình AI",
                JOptionPane.QUESTION_MESSAGE,
                null,
                AIConfig.values(),
                current
        );

        if (selected == null || selected == current) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Đổi sang \"" + selected.getDisplayName() + "\" và bắt đầu ván mới?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
            setting.setAiModel(selected.getCode());
            newGame(false);
        }
    }



    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent element) {
        int x = -1, y = -1; // lưu tọa độ
        // lấy tọa độ user click
        for (int i = 0; i < Value.SIZE; i++) {
            boolean fl = false;
            for (int j = 0; j < Value.SIZE; j++) {
                if(cell[i][j] == element.getSource()) { // nếu người dùng click vào ô này
                    x = i;
                    y = j;
                    fl = true;
                    break;
                }
            }
            if(fl) break; // dừng kiểm tra
        }

        /*kiểm tra số lần click của user*/
        if(element.getClickCount() == 1) { // người dùng click dạo(click 1 lần)
            if(userClickedCell != null && userClickedCell != aiClickedCell) {
                userClickedCell.setBackground(setting.getCellColor()); // đặt lại màu clickedCell cũ
            }
            userClickedCell = cell[x][y]; // cập nhật clickedCell
            userClickedCell.setBackground(Value.CLICK_CELL_COLOR); // làm nổi bật ô được click
        }

        else if(element.getClickCount() == 2) { // người dùng chọn đánh ô này
            if(caro.isClickable(x, y)) {//nếu ô này chưa được đánh
                caro.update(x, y, Value.USER_VALUE); // update matrix
                System.out.println("\n----------------------------------------------------------------------");
                System.out.println("Nước đi của User:" + x + " " + y);

                // Cập nhật bước đi của User
                updateTableCells(x, y, Value.USER_VALUE);
                // Kiểm tra trạng thái của bàn cờ sau khi User đánh
                if(checkResult(Value.USER_VALUE)) return;
                // Nếu user không thắng và bàn cờ chưa full thì đến lượt AI đánh
                caro.nextStep();

                // Cập nhật bước đi của AI
                x = caro.getNextX();
                y = caro.getNextY();
                updateTableCells(x, y, Value.AI_VALUE);

                // Kiểm tra trạng thái của bàn cờ sau khi AI đánh
                if(checkResult(Value.AI_VALUE)) return;
            }
        }
    }

    /**
     *
     */
    private boolean checkResult(int player) {
        if(player == Value.USER_VALUE) {
            boolean result = caro.checkWinner(Value.USER_VALUE);
            if(result == true) {
                System.out.println("User thắng!");
                JOptionPane.showMessageDialog(null, "Bạn đã thắng!");
                int currentPoint = Integer.valueOf(lblUserScore.getText())+1;
                lblUserScore.setText(String.valueOf(currentPoint));
                newGame();
                return true; // kết thúc màn chơi
            }
        }
        else {
            boolean result = caro.checkWinner(Value.AI_VALUE);
            if(result == true) {
                System.out.println("AI thắng!");
                JOptionPane.showMessageDialog(null, "AI đã thắng!");
                int currentPoint = Integer.valueOf(lblAIScore.getText())+1;
                lblAIScore.setText(String.valueOf(currentPoint));
                newGame();
                return true;
            }
        }
        if(caro.isOver()) {
            System.out.println("Hòa!");
            JOptionPane.showMessageDialog(null, "Hòa!");
            newGame();
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }
}
