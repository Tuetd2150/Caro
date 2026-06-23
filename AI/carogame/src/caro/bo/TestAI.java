package caro.bo;
import java.util.Random;

public class TestAI {
    private static Random rand = new Random();
    private static int x;
    private static int y;

    public static void main(String[] args) {
        // Khởi tạo đối tượng CaroAI với mode là 1 (AI đánh trước)
        CaroAI caroAI = new CaroAI(1);

        // Chạy vòng lặp để tạo ra các bước đi cho AI
        for (int i = 0; i < 5; i++) {
            random();
            while (!caroAI.isClickable(x, y)) {
                random();
            }
            System.out.println("Nước đi của người chơi: " + x + " " + y);
            caroAI.update(x, y, 1); // Cập nhật nước đi của người chơi
            if (caroAI.checkWinner(1)) { // Kiểm tra xem người chơi có chiến thắng không
                System.out.println("Người chơi thắng!");
                break; // Kết thúc vòng lặp nếu người chơi thắng
            }

            System.out.println("Bước đi thứ " + (i + 1) + ":");
            caroAI.nextStep(); // Tạo bước đi cho AI
            System.out.println();
            if (caroAI.checkWinner(2)) { // Kiểm tra xem AI có chiến thắng không
                System.out.println("AI thắng!");
                break; // Kết thúc vòng lặp nếu AI thắng
            }
        }
    }

    private static void random() {
        x = rand.nextInt(0,4);
        y = rand.nextInt(0,4);
    }
}

