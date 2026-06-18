// Admin Password:1234

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;//extra 


// Interface for file handling operations
interface InputOutputInterface {
    String fileOutput = "Chalets.dat"; 
    public void saveAllInfo();
    public void readAllData();
}

// Custom exception for mobile validation
class InvalidMobileNo extends Exception {
    public InvalidMobileNo(String s) {
        super(s);
    }
}

// Node class for the Linked List implementation
class Node implements Serializable {
    private Chalet data;
    private Node next;

    public Node(Chalet obj) {
        data = obj;
        next = null;
    }

    public void setNext(Node nextPtr) { next = nextPtr; }
    public Node getNext() { return next; }
    public void setData(Chalet obj) { data = obj; }
    public Chalet getData() { return data; }
}

// Abstract parent class
abstract class Chalet implements Serializable {
    private int chaletNo;
    private boolean available;
    protected double price;

    public Chalet(int chaletNo, double price) {
        this.chaletNo = chaletNo;
        this.price = price;
        this.available = true;
    }

    public abstract double calculatePrice();

    public int getChaletNo() { return chaletNo; }
    public void setChaletNo(int chaletNo) { this.chaletNo = chaletNo; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String toString() {
        return "Chalet No.: " + chaletNo + ", available=" + available;
    }
}

class RegularChalet extends Chalet {
    private String type; 

    public RegularChalet(String type, int chaletNo, double price) {
        super(chaletNo, price);
        this.type = type;
    }

    public String toString() {
        return super.toString() + ", type of chalet: " + type;
    }

    public double calculatePrice() {
        double total = 0;
        if (type.equalsIgnoreCase("double"))
            total = price + 150;
        else
            total = price; 
        return total;
    }
}

class MediumChalet extends Chalet {
    private int extraRooms;
    private boolean view;

    public MediumChalet(int extraRooms, boolean view, int chaletNo, double price) {
        super(chaletNo, price);
        this.extraRooms = extraRooms;
        this.view = view;
    }

    public String toString() {
        return super.toString() + ", Number of extra rooms: " + extraRooms + 
               (view ? ", there is a nice view" : "");
    }

    public double calculatePrice() {
        double total = price + (150 * extraRooms);
        if (view) total += 200;
        return total;
    }
    
    public int getNumberOfExtraRooms() { return extraRooms; }
}

class RoyalChalet extends MediumChalet {
    private int luxuryRooms;

    public RoyalChalet(int luxuryRooms, int extraRooms, boolean view, int chaletNo, double price) {
        super(extraRooms, view, chaletNo, price);
        this.luxuryRooms = luxuryRooms;
    }

    public String toString() {
        return super.toString() + "\nNumber of luxury rooms in Royal Chalet: " + luxuryRooms;
    }

    public double calculatePrice() {
        return super.calculatePrice() + (350 * luxuryRooms);
    }

    public int getLuxuryRooms() { return luxuryRooms; }
}

class Reservation implements Serializable {
    private String FullName;
    private String Mobile;
    private String ID;
    private Chalet chalet;
    private int NumOfDays;
    private double TotalPrice;

    public Reservation(String FullName, String Mobile, String ID) {
        this.FullName = FullName;
        this.Mobile = Mobile;
        this.ID = ID;
    }

    public Reservation(Reservation obj) {
        this.FullName = obj.FullName;
        this.Mobile = obj.Mobile;
        this.ID = obj.ID;
        this.NumOfDays = obj.NumOfDays;
        this.chalet = obj.chalet;
        this.TotalPrice = obj.TotalPrice;
    }

    public void CheckIn(Chalet chalet, int days) {
        this.NumOfDays = days;
        this.chalet = chalet;
        chalet.setAvailable(false);
        this.TotalPrice = NumOfDays * this.chalet.calculatePrice();
    }

    public void CheckOut() {
        if(chalet.isAvailable())
            JOptionPane.showMessageDialog(null, "This reservation is already checked out before.");
        else {
            chalet.setAvailable(true);
            JOptionPane.showMessageDialog(null, "Check out is done.");
        }
    }

    public String toString() {
        return "FullName: " + FullName + ", Mobile=" + Mobile +
               "\nID=" + ID + "\nchalet=" + chalet +
               "\nNumOfDays=" + NumOfDays +
               "\nTotalPrice: " + TotalPrice;
    }

    public String getFullName() { return FullName; }
    public String getMobile() { return Mobile; }
    public String getID() { return ID; }
    public Chalet getChalet() { return chalet; }
}

// Manager class handling the logic
class ChaletPark implements InputOutputInterface {
    private String ChaletParkName;
    Node headChalet; 
    Reservation[] reservations; 
    int numRes;
    int maxChalet = 100;

    public ChaletPark(String name, int maxReservations) {
        ChaletParkName = name;
        headChalet = null;
        reservations = new Reservation[maxReservations];
        numRes = 0;
    }

    public int countChalet() {
        if (headChalet == null) return 0;
        int count = 0;
        Node current = headChalet;
        while (current != null) {
            count++;
            current = current.getNext();
        }
        return count;
    }

    // Add chalet using aggregation
    public boolean addChalet(Chalet chalet) {
        if (countChalet() < maxChalet) {
            Node n = new Node(chalet);
            n.setNext(headChalet); 
            headChalet = n;
            return true;
        }
        return false;
    }

    public boolean deleteChalet(int no) {
        if (headChalet == null) return false;
        
        // check head
        if (headChalet.getData().getChaletNo() == no) {
            Chalet temp = headChalet.getData();
            if(!temp.isAvailable()) {
                JOptionPane.showMessageDialog(null, "This chalet is reserved already by other person");
                return false;
            }
            headChalet = headChalet.getNext();
            return true;
        }

        Node priv = headChalet;
        Node current = headChalet.getNext();
        while (current != null) {
            if (current.getData().getChaletNo() == no) {
                if (current.getData().isAvailable()) {
                    priv.setNext(current.getNext());
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "This chalet is reserved already by other person");
                    return false;
                }
            } else {
                priv = priv.getNext();
                current = current.getNext();
            }
        }
        return false;
    }

    // Add reservation using composition
    public boolean addReservation(Reservation res) {
        if (numRes < reservations.length) {
            reservations[numRes] = new Reservation(res); 
            numRes++;
            return true;
        }
        return false;
    }

    public boolean cancelReservation(String id, int chaletNo) {
        for(int i=0; i < numRes; i++) {
            if (reservations[i].getID().equals(id) && 
                reservations[i].getChalet().getChaletNo() == chaletNo) {
                
                reservations[i].CheckOut();
                reservations[i] = reservations[numRes-1]; // swap
                numRes--;
                reservations[numRes] = null;
                return true;
            }
        }
        return false;
    }

    public Reservation searchReservation(String id, int chaletNo) {
        for(int i=0; i < numRes; i++) {
            if (reservations[i].getID().equals(id) && 
                reservations[i].getChalet().getChaletNo() == chaletNo)
                return reservations[i];
        }
        return null;
    }

    public Chalet searchChalet(int no) {
        if (headChalet == null) return null;
        Node current = headChalet;
        while (current != null) {
            if (current.getData().getChaletNo() == no)
                return current.getData();
            current = current.getNext();
        }
        return null;
    }

    public RegularChalet[] getRegularChalet() {
        RegularChalet[] list = new RegularChalet[countChalet()];
        int j=0;
        Node current = headChalet;
        while (current != null) {
            if (current.getData() instanceof RegularChalet)
                list[j++] = (RegularChalet) current.getData();
            current = current.getNext();
        }
        return list;
    }

    public MediumChalet[] getAllMediumChalet() {
        MediumChalet[] list = new MediumChalet[countChalet()];
        int j=0;
        Node current = headChalet;
        while (current != null) {
            if (current.getData() instanceof MediumChalet)
                list[j++] = (MediumChalet) current.getData();
            current = current.getNext();
        }
        return list;
    }

    // overloading
    public RoyalChalet[] getAllRoyalChalet(int numberOfExtraRooms) {
        RoyalChalet[] list = new RoyalChalet[countChalet()];
        int j=0;
        Node current = headChalet;
        while (current != null) {
            if (current.getData() instanceof RoyalChalet && 
                ((RoyalChalet) current.getData()).getNumberOfExtraRooms() >= numberOfExtraRooms)
                list[j++] = (RoyalChalet) current.getData();
            current = current.getNext();
        }
        return list;
    }

    public Chalet[] getAllAvailableChalets() {
        Chalet[] list = new Chalet[countChalet()];
        int j=0;
        Node current = headChalet;
        while (current != null) {
            if (current.getData().isAvailable())
                list[j++] = current.getData();
            current = current.getNext();
        }
        return list;
    }

    public void saveAllInfo() {
        try {
            File out = new File(fileOutput);
            FileOutputStream fos = new FileOutputStream(out);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(headChalet); 
            oos.close();

            File out2 = new File("Reservations.dat");
            FileOutputStream fos2 = new FileOutputStream(out2);
            ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
            oos2.writeInt(numRes);
            oos2.writeObject(reservations); 
            oos2.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public void readAllData() {
        try {
            File f = new File(fileOutput);
            FileInputStream ff = new FileInputStream(f);
            ObjectInputStream in = new ObjectInputStream(ff);
            headChalet = (Node) in.readObject();
            in.close();

            File f2 = new File("Reservations.dat");
            FileInputStream ff2 = new FileInputStream(f2);
            ObjectInputStream in2 = new ObjectInputStream(ff2);
            numRes = in2.readInt();
            reservations = (Reservation[]) in2.readObject();
            in2.close();

            JOptionPane.showMessageDialog(null, "All data in files are loaded.");
        } catch (ClassNotFoundException ex) { System.out.println(ex.toString()); }
          catch (IOException e) { System.out.println(e.toString()); }
    }
}

class FirstFrame extends JFrame implements ActionListener {
    JButton jButtonCustomer;
    JButton jButtonAdmin;
    JButton jButtonExit;
    JLabel jLabeltitle;
    Container contentPane;

    public FirstFrame() {
        contentPane = getContentPane();
        contentPane.setLayout(null);
        setTitle("Chalet Reservation system");
        setLocation(200, 150);
        setResizable(false);
        setSize(700, 400);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        jLabeltitle = new JLabel("welcome in our system: ");
        jLabeltitle.setFont(new java.awt.Font("Segoe UI", 1, 36));
        jLabeltitle.setBounds(50, 20, 500, 50);
        contentPane.add(jLabeltitle);

        jButtonCustomer = new JButton("Customer");
        jButtonCustomer.setBounds(30, 100, 120, 30);
        contentPane.add(jButtonCustomer);
        jButtonCustomer.addActionListener(this);

        jButtonAdmin = new JButton("Admin");
        jButtonAdmin.setBounds(30, 150, 120, 30);
        contentPane.add(jButtonAdmin);
        jButtonAdmin.addActionListener(this);

        jButtonExit = new JButton("Exit");
        jButtonExit.setBounds(30, 200, 120, 30);
        contentPane.add(jButtonExit);
        jButtonExit.addActionListener(this);
    }

    public void actionPerformed(ActionEvent event) {
        if(event.getSource().equals(jButtonCustomer)) {
            CustomerFrame cFram = new CustomerFrame();
            cFram.setVisible(true);
        }
        else if(event.getSource().equals(jButtonAdmin)) {
            String pass = "1234";
            String str = JOptionPane.showInputDialog("Enter password");
            if(str != null && str.equals(pass)) {
                AdminFrame aFram = new AdminFrame();
                aFram.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "invalid password");
            }
        }
        else if (event.getSource().equals(jButtonExit)) {
            TestChaletPark.chaletPark.saveAllInfo();
            JOptionPane.showMessageDialog(this, "save all data is done. \n good by.");
            System.exit(0);
        }
    }
}

class AdminFrame extends JFrame implements ActionListener {
    JButton jButtonDeleteChalet;
    JButton jButtonBack;
    JButton jButtonExit;
    JButton jButtonView;
    JLabel jLabelTitle;
    JLabel jLabel2;
    JPanel jPanel1;
    JTextArea TextArea1;
    JTextField jTextField1;
    Container contentPane;

    public AdminFrame() {
        contentPane = getContentPane();
        contentPane.setLayout(null);
        setTitle("Chalet Reservation system");
        setLocation(200, 150);
        setResizable(false);
        setSize(700, 500);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        jLabelTitle = new JLabel("Admin menu: ");
        jLabelTitle.setFont(new java.awt.Font("Segoe UI", 2, 26));
        jLabelTitle.setBounds(50, 20, 500, 50);
        contentPane.add(jLabelTitle);

        jLabel2 = new JLabel("Enter chalet number: ");
        jLabel2.setBounds(50, 90, 150, 20);
        contentPane.add(jLabel2);

        jTextField1 = new JTextField();
        jTextField1.setColumns(10);
        jTextField1.setBounds(50, 130, 120, 20);
        contentPane.add(jTextField1);

        jButtonDeleteChalet = new JButton("Delete chalet");
        jButtonDeleteChalet.setBounds(50, 170, 120, 30);
        contentPane.add(jButtonDeleteChalet);
        jButtonDeleteChalet.addActionListener(this);

        jButtonBack = new JButton("Back");
        jButtonBack.setBounds(50, 400, 70, 30);
        contentPane.add(jButtonBack);
        jButtonBack.addActionListener(this);

        jButtonExit = new JButton("Exit");
        jButtonExit.setBounds(130, 400, 70, 30);
        contentPane.add(jButtonExit);
        jButtonExit.addActionListener(this);

        jPanel1 = new JPanel();
        jPanel1.setBorder(BorderFactory.createTitledBorder("View Reservation"));
        jPanel1.setBounds(250, 20, 350, 420);
        jPanel1.setLayout(null);

        TextArea1 = new JTextArea(20, 30);
        TextArea1.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        JScrollPane scrollPane = new JScrollPane(TextArea1);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(25, 25, 300, 330);
        jPanel1.add(scrollPane);

        jButtonView = new JButton("view all reservation");
        jButtonView.setBounds(100, 365, 150, 30);
        jPanel1.add(jButtonView);
        jButtonView.addActionListener(this);

        contentPane.add(jPanel1);
    }

    public void actionPerformed(ActionEvent event) {
        if(event.getSource().equals(jButtonDeleteChalet)) {
            String strNo = jTextField1.getText();
            int no;
            try {
                no = Integer.parseInt(strNo);
            } catch(NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "invalid chalet number");
                return;
            }
            if (TestChaletPark.chaletPark.deleteChalet(no)) {
                JOptionPane.showMessageDialog(this, "delete is done");
            } else {
                JOptionPane.showMessageDialog(this, "Can't Delete");
            }
            jTextField1.setText("0");
        }
        else if(event.getSource().equals(jButtonBack)) {
            this.setVisible(false);
        }
        else if (event.getSource().equals(jButtonExit)) {
            TestChaletPark.chaletPark.saveAllInfo();
            JOptionPane.showMessageDialog(this, "save all data is done. \n good by.");
            System.exit(0);
        }
        else if (event.getSource().equals(jButtonView)) {
            TextArea1.setText("");
            Reservation[] list = TestChaletPark.chaletPark.reservations;
            int count = 0;
            for(int i=0; i < list.length; i++) {
                if(list[i] != null) {
                    TextArea1.append(list[i].toString() + "\n");
                    TextArea1.append("-----------------\n");
                    count++;
                }
            }
            if (count == 0) {
                TextArea1.append("no reservation found \n");
            }
        }
    }
}

class CustomerFrame extends JFrame implements ActionListener {
    ButtonGroup buttonGroup = new ButtonGroup();
    JButton addButton, cancelButton, checkOutButton, searchButton, backButton, exitButton;
    JComboBox comboBox;
    JLabel jLabelTitle, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6;
    JPanel panel1, panel2;
    JRadioButton radio1, radio2, radio3, radio4;
    JTextArea textArea;
    JTextField text1, text2, text3, text4;
    Container contentPane;

    public CustomerFrame() {
        contentPane = getContentPane();
        contentPane.setLayout(null);
        setTitle("customer menu system");
        setLocation(200, 100);
        setResizable(false);
        setSize(800, 550);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        jLabelTitle = new JLabel("Customer menu: ");
        jLabelTitle.setFont(new java.awt.Font("Segoe UI", 2, 26));
        jLabelTitle.setBounds(50, 20, 500, 50);
        contentPane.add(jLabelTitle);

        panel1 = new JPanel();
        panel1.setBorder(BorderFactory.createTitledBorder("Reservation"));
        panel1.setBounds(20, 80, 350, 370);
        panel1.setLayout(null);
        contentPane.add(panel1);

        panel2 = new JPanel();
        panel2.setBorder(BorderFactory.createTitledBorder("view chalets"));
        panel2.setBounds(400, 80, 350, 420);
        panel2.setLayout(null);
        contentPane.add(panel2);

        // labels
        jLabel2 = new JLabel("Chalet No.: ");
        jLabel2.setBounds(20, 20, 150, 20);
        panel1.add(jLabel2);

        jLabel3 = new JLabel("User id: ");
        jLabel3.setBounds(20, 50, 150, 20);
        panel1.add(jLabel3);

        jLabel4 = new JLabel("Full Name: ");
        jLabel4.setBounds(20, 80, 150, 20);
        panel1.add(jLabel4);

        jLabel5 = new JLabel("Mobile No.: ");
        jLabel5.setBounds(20, 110, 150, 20);
        panel1.add(jLabel5);

        jLabel6 = new JLabel("Days: ");
        jLabel6.setBounds(20, 140, 150, 20);
        panel1.add(jLabel6);

        // text fields
        text1 = new JTextField();
        text1.setColumns(10);
        text1.setBounds(180, 20, 120, 20);
        panel1.add(text1);

        text2 = new JTextField();
        text2.setColumns(10);
        text2.setBounds(180, 50, 120, 20);
        panel1.add(text2);

        text3 = new JTextField();
        text3.setColumns(10);
        text3.setBounds(180, 80, 120, 20);
        panel1.add(text3);

        text4 = new JTextField();
        text4.setColumns(10);
        text4.setBounds(180, 110, 120, 20);
        panel1.add(text4);

        String[] list = {"1", "2", "3", "4"};
        comboBox = new JComboBox(list);
        comboBox.setBounds(180, 140, 100, 20);
        panel1.add(comboBox);

        addButton = new JButton("add reservation: ");
        addButton.setBounds(20, 170, 150, 30);
        panel1.add(addButton);
        addButton.addActionListener(this);

        cancelButton = new JButton("cancel reservation: ");
        cancelButton.setBounds(20, 210, 150, 30);
        panel1.add(cancelButton);
        cancelButton.addActionListener(this);

        checkOutButton = new JButton("checkOut reservation: ");
        checkOutButton.setBounds(20, 250, 150, 30);
        panel1.add(checkOutButton);
        checkOutButton.addActionListener(this);

        searchButton = new JButton("search reservation: ");
        searchButton.setBounds(20, 290, 150, 30);
        panel1.add(searchButton);
        searchButton.addActionListener(this);

        backButton = new JButton("Back");
        backButton.setBounds(20, 460, 100, 30);
        contentPane.add(backButton);
        backButton.addActionListener(this);

        exitButton = new JButton("Exit");
        exitButton.setBounds(130, 460, 100, 30);
        contentPane.add(exitButton);
        exitButton.addActionListener(this);

        textArea = new JTextArea(20, 30);
        textArea.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(25, 25, 300, 300);
        panel2.add(scrollPane);

        radio1 = new JRadioButton("Regular Chalet");
        radio1.setBounds(25, 330, 120, 30);
        panel2.add(radio1);
        radio1.addActionListener(this);

        radio2 = new JRadioButton("Medium Chalet");
        radio2.setBounds(155, 330, 120, 30);
        panel2.add(radio2);
        radio2.addActionListener(this);

        radio3 = new JRadioButton("Royal Chalet");
        radio3.setBounds(25, 370, 120, 30);
        panel2.add(radio3);
        radio3.addActionListener(this);

        radio4 = new JRadioButton("Available Chalet");
        radio4.setBounds(155, 370, 120, 30);
        panel2.add(radio4);
        radio4.addActionListener(this);

        buttonGroup.add(radio1);
        buttonGroup.add(radio2);
        buttonGroup.add(radio3);
        buttonGroup.add(radio4);
    }

    public void actionPerformed(ActionEvent event) {
        if(event.getSource().equals(addButton)) {
            int chaletNo;
            try {
                chaletNo = Integer.parseInt(text1.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "chalet number must be only digit, try again");
                return;
            }

            Chalet chaletObj = TestChaletPark.chaletPark.searchChalet(chaletNo);
            if (chaletObj == null || chaletObj.isAvailable() == false) {
                JOptionPane.showMessageDialog(this, "this chalet is not available or does not exist, try again");
                return;
            }

            try {
                String id = text2.getText();
                String name = text3.getText();
                String mob = text4.getText();
                
                if (mob.length() != 10 || !mob.startsWith("05"))
                    throw new InvalidMobileNo("mobile number must start with 05 and only have 10 digits");
                
                long num = Long.parseLong(mob); 
                
                int days = comboBox.getSelectedIndex() + 1;
                Reservation res = new Reservation(name, mob, id);
                res.CheckIn(chaletObj, days);
                TestChaletPark.chaletPark.addReservation(res);
                
                JOptionPane.showMessageDialog(this, "reservation is added successfully, be happy with our park");
                JOptionPane.showMessageDialog(this, res.toString());
                
                text1.setText("");
                text2.setText("");
                text3.setText("");
                text4.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "mobile must be only digit, try again");
            } catch (InvalidMobileNo ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
        else if(event.getSource().equals(cancelButton)) {
            int chaletNo;
            try {
                chaletNo = Integer.parseInt(text1.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "chalet number must only digit, try again");
                return;
            }
            String id = text2.getText();
            if (TestChaletPark.chaletPark.cancelReservation(id, chaletNo))
                JOptionPane.showMessageDialog(this, "cancel done.");
            else
                JOptionPane.showMessageDialog(this, "sorry can't cancel.");
            
            text1.setText("");
            text2.setText("");
            text3.setText("");
            text4.setText("");
        }
        else if(event.getSource().equals(checkOutButton)) {
            int chaletNo;
            try {
                chaletNo = Integer.parseInt(text1.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "chalet number must only digit, try again");
                return;
            }
            String id = text2.getText();
            Reservation res = TestChaletPark.chaletPark.searchReservation(id, chaletNo);
            if (res == null)
                JOptionPane.showMessageDialog(this, "can't find this reservation");
            else
                res.CheckOut();
            
            text1.setText("");
            text2.setText("");
            text3.setText("");
            text4.setText("");
        }
        else if(event.getSource().equals(searchButton)) {
            int chaletNo;
            try {
                chaletNo = Integer.parseInt(text1.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "chalet number must be only be a digit, try again");
                return;
            }
            String id = text2.getText();
            Reservation res = TestChaletPark.chaletPark.searchReservation(id, chaletNo);
            if (res == null)
                JOptionPane.showMessageDialog(this, "can't find this reservation");
            else
                JOptionPane.showMessageDialog(this, res.toString());
            
            text1.setText("");
            text2.setText("");
            text3.setText("");
            text4.setText("");
        }
        else if(event.getSource().equals(backButton)) {
            this.setVisible(false);
        }
        else if(event.getSource().equals(exitButton)) {
            TestChaletPark.chaletPark.saveAllInfo();
            JOptionPane.showMessageDialog(this, "save all data is done. \n good by.");
            System.exit(0);
        }
        else if(event.getSource().equals(radio1) && radio1.isSelected()) {
            textArea.setText("");
            RegularChalet[] list = TestChaletPark.chaletPark.getRegularChalet();
            for(int i=0; i < list.length; i++)
                if (list[i] != null)
                    textArea.append(list[i].toString() + "\n");
        }
        else if (event.getSource().equals(radio2) && radio2.isSelected()) {
            textArea.setText("");
            MediumChalet[] list2 = TestChaletPark.chaletPark.getAllMediumChalet();
            for(int i=0; i<list2.length; i++)
                if (list2[i] != null && !(list2[i] instanceof RoyalChalet))
                    textArea.append(list2[i].toString() + "\n");
        }
        else if(event.getSource().equals(radio3) && radio3.isSelected()) {
            textArea.setText("");
            int num;
            try {
                num = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter number of luxury rooms that you want: "));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, ex.toString());
                return;
            }
            RoyalChalet[] list3 = TestChaletPark.chaletPark.getAllRoyalChalet(num);
            for(int i=0; i<list3.length; i++)
                if (list3[i] != null)
                    textArea.append(list3[i].toString() + "\n");
        }
        else if (event.getSource().equals(radio4) && radio4.isSelected()) {
            textArea.setText("");
            Chalet[] list4 = TestChaletPark.chaletPark.getAllAvailableChalets();
            for(int i=0; i<list4.length; i++) {
                if (list4[i] != null) {
                    textArea.append(list4[i].getClass().getSimpleName());
                    textArea.append(list4[i].toString() + "\n");
                }
            }
        }
    }
}

public class TestChaletPark {
    static ChaletPark chaletPark = new ChaletPark("Sama Chalets", 1000);

    public static void main(String[] args) {
        File f = new File("Chalets.dat");
        File f2 = new File("Reservations.dat");
        
        if(f.exists() && f2.exists()) {
            chaletPark.readAllData();
        } else {
            RegularChalet reg1 = new RegularChalet("Single", 101, 350);
            RegularChalet reg2 = new RegularChalet("Single", 102, 300);
            RegularChalet reg3 = new RegularChalet("dobule", 103, 450); 
            RegularChalet reg4 = new RegularChalet("double", 104, 450);
            
            MediumChalet suit1 = new MediumChalet(2, true, 205, 900);
            MediumChalet suit2 = new MediumChalet(0, false, 206, 800);
            MediumChalet suit3 = new MediumChalet(1, true, 207, 1000);
            
            RoyalChalet royal1 = new RoyalChalet(2, 2, true, 308, 3500);
            RoyalChalet royal2 = new RoyalChalet(3, 0, true, 309, 5500);
            
            chaletPark.addChalet(reg1);
            chaletPark.addChalet(reg2);
            chaletPark.addChalet(reg3);
            chaletPark.addChalet(reg4);
            chaletPark.addChalet(suit1);
            chaletPark.addChalet(suit2);
            chaletPark.addChalet(suit3);
            chaletPark.addChalet(royal1);
            chaletPark.addChalet(royal2);
        }
        
        FirstFrame fram = new FirstFrame();
        fram.setVisible(true);
    }
}