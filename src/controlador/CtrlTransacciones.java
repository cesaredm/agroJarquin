/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
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
public class CtrlTransacciones implements ActionListener, CaretListener {

	IMenu menu;
	Transacciones transaccion;
	Date fecha;
	Reportes r;
	CtrlReportes ctrlR;
	DefaultTableModel modelo;
	String id;

	public CtrlTransacciones(IMenu menu, Transacciones gastos) {
		this.fecha = new Date();
		this.menu = menu;
		this.transaccion = gastos;
		r = new Reportes();
		ctrlR = new CtrlReportes(menu, r);
		this.modelo = new DefaultTableModel();
		menu.cmbCajaTransaccion.setModel(transaccion.mostrarCajas());
		this.menu.btnGuardarGasto.addActionListener(this);
		this.menu.btnActualizarGasto.addActionListener(this);
		this.menu.btnNuevoGasto.addActionListener(this);
		this.menu.EditarGastos.addActionListener(this);
		this.menu.EliminarGasto.addActionListener(this);
		MostrarGastos("");
		DeshabilitarGastos();
		this.menu.jcFechaGasto.setDate(fecha);
	}

	public void guardar() {
		float montoGasto;
		String monto = menu.txtMontoGasto.getText(),
			descripcion = menu.txtDescripcionGasto.getText(),
			caja = (String) menu.cmbCajaTransaccion.getSelectedItem(),
			tipoTransac = (String) menu.cmbTipoTransaccion.getSelectedItem(),
			moneda = this.menu.cmbMonedaTransaccion.getSelectedItem().toString();
		int idCaja = transaccion.IdCaja(caja);
		Date fecha = menu.jcFechaGasto.getDate();
		long f = fecha.getTime();
		java.sql.Date fechaGasto = new java.sql.Date(f);
		if (!monto.equals("") && !descripcion.equals("")) {
			if (isNumeric(monto)) {
				montoGasto = Float.parseFloat(monto);
				transaccion.Guardar(montoGasto, fechaGasto, descripcion, tipoTransac, idCaja, moneda);
				MostrarGastos("");
				LimpiarGastos();
				ctrlR.MostrarReportesDario(this.fecha);
				ctrlR.reportesDiarios(this.fecha);
				ctrlR.SumaTotalFiltroReporte(this.fecha, this.fecha);
				ctrlR.ReporteGlobal();
				menu.btnActualizarGasto.setEnabled(false);
				menu.btnGuardarGasto.setEnabled(true);
			}
		} else {

		}
	}

	public void actualizar() {
		float montoGasto;
		int id = Integer.parseInt(this.id);
		String monto = menu.txtMontoGasto.getText(),
			descripcion = menu.txtDescripcionGasto.getText(),
			caja = (String) menu.cmbCajaTransaccion.getSelectedItem(),
			tipoTransac = (String) menu.cmbTipoTransaccion.getSelectedItem(),
			moneda = this.menu.cmbMonedaTransaccion.getSelectedItem().toString();
		int idCaja = transaccion.IdCaja(caja);
		Date fecha = menu.jcFechaGasto.getDate();
		long f = fecha.getTime();
		java.sql.Date fechaGasto = new java.sql.Date(f);
		if (!monto.equals("") && !descripcion.equals("")) {
			if (isNumeric(monto)) {
				montoGasto = Float.parseFloat(monto);
				transaccion.Actualizar(id, montoGasto, fechaGasto, descripcion, tipoTransac, idCaja, moneda);
				MostrarGastos("");
				ctrlR.MostrarReportesDario(this.fecha);
				ctrlR.reportesDiarios(this.fecha);
				ctrlR.SumaTotalFiltroReporte(this.fecha, this.fecha);
				ctrlR.ReporteGlobal();
				LimpiarGastos();
				menu.btnActualizarGasto.setEnabled(false);
				menu.btnGuardarGasto.setEnabled(true);
			}
		} else {

		}
	}

	public void editar() {
		int filaseleccionada = menu.tblGastos.getSelectedRow();
		int id;
		try {
			if (filaseleccionada == -1) {

			} else {
				this.modelo = (DefaultTableModel) menu.tblGastos.getModel();
				id = Integer.parseInt(this.modelo.getValueAt(filaseleccionada, 0).toString());
				HabilitarGastos();
				LimpiarGastos();
				menu.txtMontoGasto.setText("" + this.transaccion.getMonto());
				menu.txtDescripcionGasto.setText(this.transaccion.getDescripcion());
				menu.jcFechaGasto.setDate(this.transaccion.getFecha());
				menu.cmbCajaTransaccion.setSelectedItem("CAJA1");
				menu.cmbTipoTransaccion.setSelectedItem(this.transaccion.getTipoTransacion());
				menu.cmbMonedaTransaccion.setSelectedItem(this.transaccion.getMoneda());
				this.id = String.valueOf(id);
				menu.btnActualizarGasto.setEnabled(true);
				menu.btnGuardarGasto.setEnabled(false);
			}
		} catch (Exception err) {
			JOptionPane.showMessageDialog(null, err + "en la funcion de EditarGastos");
		}
	}

	public void eliminar() {
		int filaseleccionada = menu.tblGastos.getSelectedRow(), id = 0, confirmacion = 0;
		try {
			if (filaseleccionada == -1) {

			} else {
				confirmacion = JOptionPane.showConfirmDialog(
					null,
					"Seguro que quieres borrar este gasto",
					"Advertencia",
					JOptionPane.WARNING_MESSAGE
				);
				if (confirmacion == JOptionPane.YES_OPTION) {
					this.modelo = (DefaultTableModel) menu.tblGastos.getModel();
					id = Integer.parseInt(this.modelo.getValueAt(filaseleccionada, 0).toString());
					this.transaccion.Eliminar(id);
					MostrarGastos("");
					ctrlR.MostrarReportesDario(this.fecha);
					ctrlR.reportesDiarios(this.fecha);
					ctrlR.SumaTotalFiltroReporte(this.fecha, this.fecha);
					ctrlR.ReporteGlobal();
				}
			}
		} catch (Exception err) {
			JOptionPane.showMessageDialog(null, err + "en la funcion ElimarGasto en la clase IMenu");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == menu.btnGuardarGasto) {
			this.guardar();
		}
		if (e.getSource() == menu.btnActualizarGasto) {
			this.actualizar();
		}
		if (e.getSource() == menu.btnNuevoGasto) {
			HabilitarGastos();
			LimpiarGastos();
		}
		if (e.getSource() == menu.EditarGastos) {
			this.editar();
		}
		if (e.getSource() == menu.EliminarGasto) {
			this.eliminar();
		}
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public void MostrarGastos(String Buscar) {
		menu.tblGastos.getTableHeader().setFont(new Font("Sugoe UI", Font.PLAIN, 14));
		menu.tblGastos.getTableHeader().setOpaque(false);
		menu.tblGastos.getTableHeader().setBackground(new Color(69, 76, 89));
		menu.tblGastos.getTableHeader().setForeground(new Color(255, 255, 255));
		menu.jcFechaGasto.setDate(this.fecha);
		menu.tblGastos.setModel(transaccion.Mostrar(Buscar));
	}

	public boolean isNumeric(String cadena) {//metodo para la validacion de campos numericos
		try {
			Float.parseFloat(cadena);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public void LimpiarGastos() {
		menu.txtMontoGasto.setText("");
		menu.txtDescripcionGasto.setText("");
	}

	public void HabilitarGastos() {
		menu.txtMontoGasto.setEnabled(true);
		menu.cmbTipoTransaccion.setEnabled(true);
		menu.btnActualizarGasto.setEnabled(false);
		menu.btnGuardarGasto.setEnabled(true);
		menu.txtDescripcionGasto.setEnabled(true);
		menu.cmbCajaTransaccion.setEnabled(true);
	}

	public void DeshabilitarGastos() {
		menu.txtMontoGasto.setEnabled(false);
		menu.txtDescripcionGasto.setEnabled(false);
		menu.btnActualizarGasto.setEnabled(false);
		menu.btnGuardarGasto.setEnabled(false);
		menu.cmbCajaTransaccion.setEnabled(false);
		menu.cmbTipoTransaccion.setEnabled(false);
	}
}
