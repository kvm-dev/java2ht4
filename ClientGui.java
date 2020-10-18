package geekbrains.ru.java2.ht4;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientGui extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private static final JTextArea log = new JTextArea();
    private final JPanel panelTop = new JPanel(new GridLayout(2, 3));
    private final JTextField tfIPAddress = new JTextField("127.0.0.1");
    private final JTextField tfPort = new JTextField("8189");
    private final JCheckBox cbAlwaysOnTop = new JCheckBox("Always on top");
    private final JTextField tfLogin = new JTextField("KVM");
    private final JPasswordField tfPassword = new JPasswordField("123");
    private final JButton btnLogin = new JButton("Login");

    private final JPanel panelBottom = new JPanel(new BorderLayout());
    private final JButton btnDisconnect = new JButton("<html><b>Disconnect</b></html>");
    private final JTextField tfMessage = new JTextField();
    private final JButton btnSend = new JButton("Send");

    private final JList<String> userList = new JList<>();

    //неплохо бы видеть время, когда отправляем сообщение, можно вообще и дату, но я думаю пока никчему
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss"); // решил что часов минут и секунд хватит


    private ClientGui() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(WIDTH, HEIGHT);
        JScrollPane scrollLog = new JScrollPane(log);
        JScrollPane scrollUser = new JScrollPane(userList);
        String[] users = {"user1", "user2", "user3", "user4", "user5",
                "user_with_an_exceptionally_long_name_in_this_chat"};
        userList.setListData(users);
        scrollUser.setPreferredSize(new Dimension(100, 0));
        cbAlwaysOnTop.addActionListener(this);
        btnSend.addActionListener(this);
        panelTop.add(tfIPAddress);
        panelTop.add(tfPort);
        panelTop.add(cbAlwaysOnTop);
        panelTop.add(tfLogin);
        panelTop.add(tfPassword);
        panelTop.add(btnLogin);
        panelBottom.add(btnDisconnect, BorderLayout.WEST);
        panelBottom.add(tfMessage, BorderLayout.CENTER);
        panelBottom.add(btnSend, BorderLayout.EAST);

        add(scrollLog, BorderLayout.CENTER);
        add(scrollUser, BorderLayout.EAST);
        add(panelTop, BorderLayout.NORTH);
        add(panelBottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGui();
            }
        });
        //вот тут будем слушать изменения лога на случай появления новых символов, как впихнуть в экшн перформед не разобрался, поэтому реализовал отдельным слушателем
        log.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                //так мы смотрим не только за нашими постами в лог, но и за всеми постами вообще, например от др. пользователей
                //по идее можно былоб дописывать нужные строки, но мне пока кажется, что удобнее просто переписывать файл лога, спрошу на занятии об этом.. или комментните как правильно)
                    BufferedWriter fileLog;
                    try {
                        fileLog = new BufferedWriter(new FileWriter("chatLog.txt",
                                false));
                        log.write(fileLog);
                        fileLog.close();
                      
                    } catch (IOException ioError) {
                        JOptionPane.showMessageDialog(null, "IO File Error");
                        ioError.printStackTrace();
                    }
            }



            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                //тут пусто, т.к. мы ничего не удаляем

            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
            //как я понял какой-то сложный метод, который как-то свяазн с форматированием, нам тоже не нужен
            }
        });

    }


    @Override
    public void actionPerformed(ActionEvent e) {
      //Добавляем в лог по Enter, Нагуглил 2 решения про виртуал кей и про кнопку по событие в дефолте, сделал вариант 2, комментните если такое себе решение
        JRootPane rootPane = SwingUtilities.getRootPane(btnSend);
        rootPane.setDefaultButton(btnSend);

        Object src = e.getSource();
        if (src == cbAlwaysOnTop) {
            setAlwaysOnTop(cbAlwaysOnTop.isSelected());
        }
        else if (src == btnSend) {
            if(tfMessage.getText().length()>0) { // если у нас не пустое поле для текста тогда отправляем в лог
                LocalDateTime time = LocalDateTime.now();
                log.append(dtf.format(time) + " " + tfLogin.getText() + ": " + tfMessage.getText() + "\n");
                //разумно было бы еще очищать поле для ввода текста, как в других чатах
                tfMessage.setText("");
            }
        }


        else {
            throw new RuntimeException("Unknown action source: " + src);
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        String msg;
        StackTraceElement[] ste = e.getStackTrace();
        msg = "Exception in " + t.getName() + " " +
                e.getClass().getCanonicalName() + ": " +
                e.getMessage() + "\n\t at " + ste[0];
        JOptionPane.showMessageDialog(this, msg, "Exception", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }





}