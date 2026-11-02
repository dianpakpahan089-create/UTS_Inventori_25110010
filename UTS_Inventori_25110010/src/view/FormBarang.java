package view;

import dao.BarangDAO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Barang;

public class FormBarang extends JFrame {
    private final JTextField txtKode = new JTextField(15);
    private final JTextField txtNama = new JTextField(15);
    private final JTextField txtStok = new JTextField(15);
    private final JComboBox<String> cbKategori =
            new JComboBox<>(new String[]{"Komputer", "Jaringan", "Alat Ukur"});
    private final JButton btnSimpan = new JButton("Simpan");
    private final JButton btnUbah = new JButton("Ubah");
    private final JButton btnHapus = new JButton("Hapus");
    private final JButton btnBersihkan = new JButton("Bersihkan");
    private final DefaultTableModel model =
            new DefaultTableModel(new String[]{"Kode", "Nama Barang", "Kategori", "Stok"}, 0);
    private final JTable tabel = new JTable(model);
    private final BarangDAO dao = new BarangDAO();

    public FormBarang() {
        setTitle("Pengelolaan Data Barang Laboratorium");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ----- Panel input -----
        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        form.add(new JLabel("Kode Barang"));  form.add(txtKode);
        form.add(new JLabel("Nama Barang"));  form.add(txtNama);
        form.add(new JLabel("Kategori"));     form.add(cbKategori);
        form.add(new JLabel("Stok"));         form.add(txtStok);

        JPanel tombol = new JPanel(new FlowLayout());
        tombol.add(btnSimpan); tombol.add(btnUbah);
        tombol.add(btnHapus);  tombol.add(btnBersihkan);

        JPanel atas = new JPanel(new BorderLayout());
        atas.add(form, BorderLayout.CENTER);
        atas.add(tombol, BorderLayout.SOUTH);

        add(atas, BorderLayout.NORTH);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        // ----- Event -----
        btnSimpan.addActionListener(e -> simpan());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnBersihkan.addActionListener(e -> bersihkan());
        tabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { isiFormDariTabel(); }
        });

        tampilData();
        setSize(600, 450);
        setLocationRelativeTo(null);
    }

    // Ambil & validasi input. Return null jika tidak valid.
    private Barang ambilInput() {
        String kode = txtKode.getText().trim();
        String nama = txtNama.getText().trim();
        String stokStr = txtStok.getText().trim();

        if (kode.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode dan nama tidak boleh kosong!",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int stok;
        try {
            stok = Integer.parseInt(stokStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Stok harus berupa angka!",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (stok < 0) {
            JOptionPane.showMessageDialog(this, "Stok tidak boleh kurang dari 0!",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return new Barang(kode, nama, cbKategori.getSelectedItem().toString(), stok);
    }

    private void simpan() {
        Barang b = ambilInput();
        if (b == null) return;
        if (dao.simpan(b)) {
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan.");
            tampilData();
            bersihkan();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Data GAGAL disimpan (kode mungkin sudah ada atau koneksi bermasalah).",
                    "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ubah() {
        Barang b = ambilInput();
        if (b == null) return;
        if (dao.ubah(b)) {
            JOptionPane.showMessageDialog(this, "Data berhasil diubah.");
            tampilData();
            bersihkan();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Data GAGAL diubah. Pastikan kode barang sudah ada.",
                    "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapus() {
        String kode = txtKode.getText().trim();
        if (kode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data atau isi kode yang akan dihapus!",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int pilih = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus barang dengan kode " + kode + "?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (pilih != JOptionPane.YES_OPTION) return;

        if (dao.hapus(kode)) {
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
            tampilData();
            bersihkan();
        } else {
            JOptionPane.showMessageDialog(this, "Data GAGAL dihapus (kode tidak ditemukan).",
                    "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tampilData() {
        model.setRowCount(0);
        for (Barang b : dao.getAll()) {
            model.addRow(new Object[]{b.getKodeBarang(), b.getNamaBarang(),
                b.getKategori(), b.getStok()});
        }
    }

    private void isiFormDariTabel() {
        int r = tabel.getSelectedRow();
        if (r < 0) return;
        txtKode.setText(model.getValueAt(r, 0).toString());
        txtNama.setText(model.getValueAt(r, 1).toString());
        cbKategori.setSelectedItem(model.getValueAt(r, 2).toString());
        txtStok.setText(model.getValueAt(r, 3).toString());
    }

    private void bersihkan() {
        txtKode.setText("");
        txtNama.setText("");
        txtStok.setText("");
        cbKategori.setSelectedIndex(0);
        tabel.clearSelection();
        txtKode.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormBarang().setVisible(true));
    }
}