import java.util.Scanner;

public class Bank {
    Scanner scan = new Scanner(System.in);

    static Manager<User> userMgr = new Manager<>(); // 사용자 매니저
    static Manager<Account> accountMgr = new Manager<>(); // 계좌 매니저

    int menu = -1; // 메인 메뉴
    User loginUser = new User(); // 은행 시스템을 이용할 회원
    Account loginAccount = new Account(); // 은행 시스템을 이용할 회원의 계과

    void run() {
        setDatabase();
        login();

        System.out.println("--- 뱅크 키오스크, 환영합니다!");
        System.out.println(loginUser.name + "님으로 정상적으로 로그인됐습니다.");

        while (menu != 0) {
            System.out.println("-원하시는 메뉴를 입력해주세요.");
            System.out.print("(1) 현금입금\t\t\t(2) 현금인출\n(3) 계좌이체\t\t\t(4) 거래내역조회\n");
            if (loginUser.isSuperUser())
                System.out.print("(5) 전체 데이터 조회\t(6) 전체 계좌\n");
            System.out.print("(0) 종료 \n");
            menu = scan.nextInt();

            switch (menu) {
                case 0 -> System.exit(0);
                case 1 -> {
                    System.out.print("금액을 입력해주세요: ₩");
                    deposit();
                }
                case 2 -> {
                    System.out.print("금액을 입력해주세요: ₩");
                    withdraw();
                }
                case 3 -> transfer();
                case 4 -> showHistory();
                case 5 -> {
                    if (loginUser.isSuperUser())
                        userMgr.printAll();
                }
                case 6 -> {
                    if (loginUser.isSuperUser())
                        accountMgr.printAll();
                }
            }

            System.out.print("--- 이용해주셔서 감사합니다!\n\n");

        }

    }

    // 현금 입금
    private void deposit() {
        int cash = scan.nextInt();
        cash = Math.abs(cash); // 현금 입금은 음수가 될 수 없으므로 보정

        loginAccount.cash += cash;
        loginAccount.createHistory(1, "*Today", "-", cash); // 거래내역 생성
        System.out.println("거래 후 잔고: " + loginAccount.cash);
    }

    // 현금 출금
    private int withdraw() {
        int cash = scan.nextInt();

        while (cash > loginAccount.cash) { // 본인 잔고 확인
            System.out.println("계좌 잔고가 부족합니다. " + (loginAccount.cash - cash) + "원 부족.");
            System.out.print("금액을 입력해주세요: ₩");
            cash = scan.nextInt();
        }

        loginAccount.cash -= cash;
        loginAccount.createHistory(2, "*Today", "-", cash); // 거래내역 생성
        System.out.println("거래 후 잔고: " + loginAccount.cash);

        return cash; // 메서드 재사용 하기 위해 인출은 리턴값을 가진다.
    }

    // 이체, 타인 계좌에게 송금
    private void transfer() {
        System.out.println("송금할 계좌번호를 입력해주세요: ");

        Account account = findAccount(scan.next());

        while (account == null) {
            System.out.println("알 수 없는 계좌번호입니다. 다시입력해주세요.");
            account = findAccount(scan.next());

            if (account != null) // 계좌가 정상적으로 조회됐을경우
                break;
        }

        if (account.id.equals(loginUser.id)) { // 자신 계좌번호를 입력했을경우 거부
            System.out.println("본인계좌에 계좌이체를 할 수 없습니다.");
            return;
        }

        User user = findUser(account.id); // 해당 계좌를 사용하는 사용자 찾기

        if (user == null) {
            System.out.println("[시스템] 사용자를 찾을 수 없습니다.");
            return;
        }

        System.out.printf("예금주: %s\n", user.name);
        System.out.print("금액을 입력해주세요: ₩");

        int cash = withdraw(); // 본인 계좌에서 인출 후
        account.cash += cash; // 상대 계좌로 전달
        account.createHistory(1, "*Today", loginUser.name, cash); // 상대 거래내역 생성
    }

    // 거래내역조회
    private void showHistory() {
        Account account = findAccount(loginUser.id);
        account.printHistory();
    }

    // 데이터 마운트
    private void setDatabase() {
        // 사용자 데이터 불러오기
        userMgr.readAll("src/input/user.txt", User::new);

        // 계좌 데이터 불러오기
        accountMgr.readAll("src/input/account.txt", Account::new);
    }

    // 키오스크 이용자
    private void login() {
        while (true) {
            System.out.print("ID: ");
            String id = scan.next();
            System.out.print("PW: ");
            String pw = scan.next();

            // int id = 0; // 테스트용으로 사용자 0으로 로그인하여 시스템을 시연한다.
            loginUser = findUser(id);
            if (!isValidLogin(pw)) { // 유효하지 않은 로그인이면 다시 입력
                continue;
            }

            loginAccount = findAccount(loginUser.id);
            if (loginAccount == null) {
                System.out.println("[시스템] 계좌를 찾을 수 없습니다.");
            }
            break;
        }
    }

    private boolean isValidLogin(String pw) {
        if (loginUser == null) {
            System.out.println("[시스템] 사용자를 찾을 수 없습니다.");
            return false;
        }
        if (!loginUser.password.contentEquals(pw)) {
            System.out.println("[시스템] 비밀번호가 잘못되었습니다.");
            return false;
        }
        return true;
    }

    // 리스트에서 유저 찾기
    public User findUser(String id) {
        for (User user : userMgr.list) {
            if (user.matches(id)) {
                return user;
            }
        }
        return null;
    }

    // 리스트에서 계좌 찾기
    public Account findAccount(String number) {
        for (Account account : accountMgr.list) {
            if (account.matches(number)) {
                return account;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Bank bank = new Bank();
        bank.run();
    }
}
