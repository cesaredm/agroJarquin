package controlador;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import modelo.*;
import vista.IMenu;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class CtrlCategoria implements ActionListener, CaretListener {

    IMenu menu;
    Categorias categorias;
    DefaultTableModel modelo;
    String id;

    public CtrlCategoria(IMenu menu, Categorias C) {
        this.menu = menu;
        this.categorias = C;
        MostrarCategorias("");
        DeshabilitarCategoria();
        this.menu.btnGuardarCategoria.addActionListener(this);
        this.menu.btnActualizarCategoria.addActionListener(this);
        this.menu.btnNuevoCategoria.addActionListener(this);
        this.menu.EditarCategoria.addActionListener(this);
        this.menu.BorrarCategoria.addActionListener(this);
        this.menu.txtBuscarCategoria.addCaretListener(this);
        this.modelo = new DefaultTableModel();
        this.id = null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menu.btnGuardarCategoria) {
            String nombre = menu.txtNombreCategoria.getText(), descripcion = menu.txtDescripcionCategoria.getText();
            if (nombre.equals("")) {
                //JOptionPane.showMessageDialog(null, "Llene el campo Nombre G", "Advertencia", JOptionPane.WARNING_MESSAGE);
            } else {
                categorias.Guardar(nombre, descripcion);
                MostrarCategorias("");
                LimpiarCategoria();
            }
        }
        
        if (e.getSource() == menu.btnActualizarCategoria) {
            String nombre = menu.txtNombreCategoria.getText();
            if (nombre.equals("")) {
                //JOptionPane.showMessageDialog(null, "Llene el campo Nombre A", "Advertencia", JOptionPane.WARNING_MESSAGE);
            } else {
                categorias.Actualizar(this.id, menu.txtNombreCategoria.getText(), menu.txtDescripcionCategoria.getText());
                MostrarCategorias("");
                LimpiarCategoria();
                menu.btnGuardarCategoria.setEnabled(true);
                menu.btnActualizarCategoria.setEnabled(false);
            }
        }
        if (e.getSource() == menu.btnNuevoCategoria) {
            HabilitarCategoria();
            LimpiarCategoria();
        }
        if (e.getSource() == menu.EditarCategoria) {
            int filaseleccionada;
            String nombre, descripcion;
            try {
                filaseleccionada = menu.tblCategorias.getSelectedRow();
                if (filaseleccionada == -1) {
                    JOptionPane.showMessageDialog(null, "Seleccione una Fila", "Advertencia", JOptionPane.WARNING_MESSAGE);
                } else {
                    HabilitarCategoria();
                    LimpiarCategoria();
                    modelo = (DefaultTableModel) menu.tblCategorias.getModel();
                    nombre = (String) modelo.getValueAt(filaseleccionada, 1);
                    descripcion = (String) modelo.getValueAt(filaseleccionada, 2);
                    menu.txtNombreCategoria.setText(nombre);
                    menu.txtDescripcionCategoria.setText(descripcion);
                    this.id = (String) modelo.getValueAt(filaseleccionada, 0);
                    menu.btnGuardarCategoria.setEnabled(false);
                    menu.btnActualizarCategoria.setEnabled(true);
                }
            } catch (Exception err) {

            }
        }
        if (e.getSource() == menu.BorrarCategoria) {
            int filaseleccionada;
            String id;
            try {
                filaseleccionada = menu.tblCategorias.getSelectedRow();
                if (filaseleccionada == -1) {
                    JOptionPane.showMessageDialog(null, "Seleccione una Fila", "Advertencia", JOptionPane.WARNING_MESSAGE);
                } else {
                    int confirmar = JOptionPane.showConfirmDialog(null, "Seguro que quieres borrar esta categoria", "Avertencia", JOptionPane.OK_CANCEL_OPTION);
                    if (confirmar == JOptionPane.YES_OPTION) {
                        modelo = (DefaultTableModel) menu.tblCategorias.getModel();
                        id = (String) modelo.getValueAt(filaseleccionada, 0);
                        categorias.Eliminar(id);
                        MostrarCategorias("");
                    }
                }
            } catch (Exception err) {
                JOptionPane.showMessageDialog(null, e + "en la funcion Borrar Categoria");
            }
        }
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        if (e.getSource() == menu.txtBuscarCategoria) {
            MostrarCategorias(menu.txtBuscarCategoria.getText());
        }
    }

    //metodo para llenar la tabla categorias del formulario Categorias
    public void MostrarCategorias(String Buscar) {
        menu.tblCategorias.getTableHeader().setFont(new Font("Sugoe UI", Font.PLAIN, 14));
        menu.tblCategorias.getTableHeader().setOpaque(false);
        menu.tblCategorias.getTableHeader().setBackground(new Color(69,76,89));
        menu.tblCategorias.getTableHeader().setForeground(new Color(255, 255, 255));
        menu.tblCategorias.setModel(categorias.Consulta(Buscar));
    }

    //metodo para limpiar el formulario categoria
    public void LimpiarCategoria() {
        menu.txtNombreCategoria.setText("");
        menu.txtDescripcionCategoria.setText("");
    }

    //metodo para Habilitar los elementos inabilitados por el metodo DeshabilitarCategoria
    public void HabilitarCategoria() {
        menu.txtNombreCategoria.setEnabled(true);
        menu.txtDescripcionCategoria.setEnabled(true);
        menu.btnNuevoCategoria.setEnabled(true);
        menu.btnGuardarCategoria.setEnabled(true);
        menu.btnActualizarCategoria.setEnabled(false);
    }

    //metodo para dehabilitar elementos de formulario Categoria
    public void DeshabilitarCategoria() {
        menu.txtNombreCategoria.setEnabled(false);
        menu.txtDescripcionCategoria.setEnabled(false);
        menu.btnNuevoCategoria.setEnabled(true);
        menu.btnGuardarCategoria.setEnabled(false);
        menu.btnActualizarCategoria.setEnabled(false);
    }
}
