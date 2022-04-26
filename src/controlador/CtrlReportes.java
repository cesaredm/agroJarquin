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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import modelo.Reportes;
import vista.IMenu;
import javax.swing.table.DefaultTableModel;
import modelo.PagosCreditos;
import modelo.AperturasYcierres;
import modelo.InfoFactura;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class CtrlReportes implements ActionListener, MouseListener, KeyListener {

    IMenu menu;
    Reportes reportes;
    PagosCreditos pagosC;
    AperturasYcierres aperturas;
    SpinnerNumberModel sModel;
    JSpinner spiner;
    Date fecha;
    DefaultTableModel modelo;
    String idCliente = "";
    DecimalFormat formato;
    InfoFactura info;
    private boolean estadoC = true;

    public CtrlReportes(IMenu menu, Reportes reportes) {
        this.menu = menu;
        this.reportes = reportes;
        this.pagosC = new PagosCreditos();
        this.aperturas = new AperturasYcierres();
        this.fecha = new Date();
        this.info = new InfoFactura();
        this.menu.btnReporteDiario.addActionListener(this);
        this.menu.btnReporteDiario.setActionCommand("REPORTE-DIARIO");
        this.menu.btnMostraTotalFacturado.addActionListener(this);
        this.menu.btnMostraTotalFacturado.setActionCommand("MOSTRAR-FACTURADO");
        this.menu.btnMasReportes.addActionListener(this);
        this.menu.btnMasReportes.setActionCommand("MAS-REPORTES");
        this.menu.btnAperturaCaja.addActionListener(this);
        this.menu.btnAperturaCaja.setActionCommand("APERTURA-CAJA");
        this.menu.btnGuardarApertura.addActionListener(this);
        this.menu.btnGuardarApertura.addKeyListener(this);
        this.menu.txtEfectivoApertura.addKeyListener(this);
        this.menu.btnGuardarApertura.setActionCommand("GUARDAR-APERTURA");
        this.menu.btnAddTotalV.addActionListener(this);
        this.menu.btnAddTotalV.setActionCommand("ADD-TOTALV");
        this.menu.tblReporte.addMouseListener(this);
        this.menu.btnMostrarInversion.addActionListener(this);
        this.menu.btnMostrarInversion.setActionCommand("MOSTRAR-INVERSION");
        this.menu.btnProductosMasVendidos.addActionListener(this);
        this.menu.btnProductosMasVendidos.setActionCommand("PRODUCTOS-MAS-VENDIDOS");
        this.menu.btnMostrarPmasV.addActionListener(this);
        this.menu.btnMostrarPmasV.setActionCommand("MOSTRARPMASV");
        this.menu.btnImprimirPmasV.addActionListener(this);
        this.menu.btnImprimirPmasV.setActionCommand("IMPRIMIRPMASV");
        this.menu.btnMostrarFacturasEmitidas.addActionListener(this);
        this.menu.btnMostrarFacturasEmitidas.setActionCommand("MOSTRARFACTURASEMITIDAS");
        this.menu.btnDevolverProducto.addActionListener(this);
        this.menu.btnDevolverProducto.setActionCommand("DEVOLVER-PRODUCTO");
        this.menu.btnBuscarFiltroReporte.addActionListener(this);
        this.menu.txtBuscarFactura.addKeyListener(this);
        this.menu.btnEnviarRD.addActionListener(this);
        this.menu.btnEnviarCorreo.addActionListener(this);
	this.menu.btnMostrarUtilidades.addActionListener(this);
	this.menu.btnActualizarListaProductoDiarios.addActionListener(this);
        EstiloTablaTotalV();
        formato = new DecimalFormat("###,###,###,###,##0.00");
        iniciar();
    }

    public void iniciar() {
        reportesDiarios(this.fecha);
        MostrarReportesDario(this.fecha);
        mostrarProductosMasVendidios(this.fecha, this.fecha);
        ReporteGlobal();
        this.menu.jcFechaReporteDario.setDate(this.fecha);
        this.menu.jcFecha1.setDate(this.fecha);
        this.menu.jcFecha2.setDate(this.fecha);
        this.menu.jc1.setDate(this.fecha);
        this.menu.jc2.setDate(this.fecha);
        this.menu.jcFechaReport1.setDate(this.fecha);
        this.menu.jcFechaReport2.setDate(this.fecha);
        this.menu.jcFacturasEmitidas.setDate(this.fecha);
        SumaTotalFiltroReporte(this.fecha, this.fecha);
        inversion();
    }

    public boolean getEstadoC() {
        return estadoC;
    }

    public void setEstadoC(boolean estadoC) {
        this.estadoC = estadoC;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if (e.getSource() == menu.btnReporteDiario) {
            Date fecha1 = menu.jcFechaReporteDario.getDate(), fecha2 = menu.jcFecha2.getDate();
            reportesDiarios(fecha1);

        }
        if (e.getSource() == menu.btnMostrarFacturasEmitidas) {
            Date fecha = menu.jcFacturasEmitidas.getDate();
            MostrarReportesDario(fecha);
        }
        if (e.getSource() == menu.btnMostraTotalFacturado) {
            Date fecha1 = menu.jcFecha1.getDate(), fecha2 = menu.jcFecha2.getDate();
            long fe1 = fecha1.getTime(), fe2 = fecha2.getTime();
            java.sql.Date fechaInicio = new java.sql.Date(fe1);//convertir la fecha a formato sql
            java.sql.Date fechaFinal = new java.sql.Date(fe2);//convertir la fecha a formato sql
            menu.lblTotalFacturado.setText("" + reportes.IngresosTotales(fechaInicio, fechaFinal));
        }
        if (e.getSource() == menu.btnMasReportes) {
            menu.ventanaMasReportes.setSize(1197, 440);
            menu.ventanaMasReportes.setLocationRelativeTo(null);
            menu.ventanaMasReportes.setVisible(true);
        }
        if (e.getSource() == menu.btnAperturaCaja) {
            menu.AperturasCaja.setSize(395, 290);
            menu.AperturasCaja.setLocationRelativeTo(null);
            this.menu.cmbCajasApertura.setModel(aperturas.mostrarCajas());
            menu.AperturasCaja.setVisible(true);
            menu.jcFechaApertura.setDate(this.fecha);
            ValidarExistApertura();
        }
        if (e.getSource() == menu.btnMostrarInversion) {
            inversion();
        }
        if (e.getSource().equals(menu.btnGuardarApertura)) {
            GuardarAperturas();
            menu.AperturasCaja.setVisible(false);
        }
        if (e.getSource().equals(menu.btnAddTotalV)) {
            if (!menu.lblTotalFacturado.getText().equals("")) {
                Date fechaInicio = menu.jcFecha1.getDate(), fechaFinal = menu.jcFecha2.getDate();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");
                String fecha1 = sdf.format(fechaInicio), fecha2 = sdf.format(fechaFinal), total = menu.lblTotalFacturado.getText();
                String[] fila = {fecha1, fecha2, total};
                this.modelo = (DefaultTableModel) menu.tblMostrarTotalV.getModel();
                this.modelo.addRow(fila);
                menu.lblTotalFacturado.setText("");
            } else {

            }
        }
        if (e.getSource() == menu.btnGuardarCierre) {
            RealizarCorte();
        }
        if (e.getSource() == menu.btnProductosMasVendidos) {
            menu.ventanaProductosMasVendidos.setLocationRelativeTo(null);
            menu.ventanaProductosMasVendidos.setSize(1165, 350);
            menu.ventanaProductosMasVendidos.setVisible(true);
        }
        if (e.getSource() == menu.btnMostrarPmasV) {
            Date fecha1 = menu.jc1.getDate(), fecha2 = menu.jc2.getDate();
            mostrarProductosMasVendidios(fecha1, fecha2);
        }
        if (e.getSource() == menu.btnBuscarFiltroReporte) {
            Date fecha1 = menu.jcFechaReport1.getDate(), fecha2 = menu.jcFechaReport2.getDate();
            SumaTotalFiltroReporte(fecha1, fecha2);
        }
        if (e.getSource() == menu.btnEnviarRD) {
            menu.EnviarRD.setSize(460, 111);
            menu.EnviarRD.setLocationRelativeTo(null);
            menu.EnviarRD.setVisible(true);
        }
        if (e.getSource() == menu.btnEnviarCorreo) {

            String correo = menu.txtCorreo.getText();
            if (!correo.equals("")) {
                EnviarCorreoRD();
                menu.txtCorreo.setText("");
            } else {

            }
        }
	if(e.getSource() == menu.btnMostrarUtilidades)
	{
		float precioDolar = Float.parseFloat(menu.txtPrecioDolarVenta.getText());
		String year = String.valueOf(menu.jcYear.getYear());	
		menu.tblUtilidades.setModel(this.reportes.utilidadesAnualesXmes(year, precioDolar));
	}
	if(e.getSource() == this.menu.btnActualizarListaProductoDiarios){
		this.menu.tblListaProductosDiarios.setModel(this.reportes.mostrarProductosVendidosDiarios());	
	}
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if (e.getSource() == menu.tblReporte) {
            if (e.getClickCount() == 2) {
                int id = 0, filaseleccionada = menu.tblReporte.getSelectedRow();
                try {
                    if (filaseleccionada == -1) {

                    } else {
                        this.modelo = (DefaultTableModel) menu.tblReporte.getModel();
                        id = Integer.parseInt(this.modelo.getValueAt(filaseleccionada, 0).toString());
                        MostrarDetalleFactura(id);
                        menu.vistaDetalleFacturas.setSize(782, 320);//762, 363
                        menu.vistaDetalleFacturas.setVisible(true);
                        menu.vistaDetalleFacturas.setLocationRelativeTo(null);
                    }
                } catch (Exception err) {
                    JOptionPane.showMessageDialog(null, err);
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    //para la inversion
    public void inversion() {

        float cordobas = this.reportes.proyeccionVentaCordobas(),
                dolar = this.reportes.proyeccionVentaDolar(),
                precioDolar = Float.parseFloat(menu.txtPrecioDolarVenta.getText()),
                total = 0;
        if (menu.isNumeric(String.valueOf(precioDolar))) {
            total = cordobas + (dolar * precioDolar);
            menu.lblProyeccion.setText("" + this.formato.format(total));
            menu.lblInversionDolar.setText(""+this.reportes.inversionDolar());
            menu.lblInversionCordobas.setText(""+this.reportes.inversionCordobas());
        }
    }

    

    public void reportesDiarios(Date fecha) {
        float totalVendidio = 0, 
                exisCaja = 0, 
                ingresosVentaE = 0, 
                ingresosVentasT = 0, 
                ingresosPagosE, 
                ingresosPagoT = 0, 
                ingresosEfectivo = 0, 
                Ingresosbancos = 0, 
                creditos = 0, 
                egresos = 0, 
                base = 0, 
                dolaresComprados = 0, 
                dolaresVendidos = 0,
                totalCordobasPorCompraDolar = 0,
                totalCordobasPorVentaDolar = 0,
                diferenciaEnCordobas = 0,
                utilidad = 0;
        float precioCompraDolar = Float.parseFloat(menu.txtPrecioDolarCompra.getText()),
                precioVentaDolar = Float.parseFloat(menu.txtPrecioDolarVenta.getText());
        long f1 = fecha.getTime();
        java.sql.Date fechaInicio = new java.sql.Date(f1);//convertir la fecha a formato sql
        //base
        base = reportes.baseEfectivoDiario(fechaInicio);
        //ingresos por ventas en efectivo
        ingresosVentaE = reportes.ingresoEfectivoCajaDiario(fechaInicio);
        //ingresos por ventas cobradas con tarjeta
        ingresosVentasT = reportes.IngresoAbancosDiario(fechaInicio);
        //ingresos por pagos cobrados en efectivo
        ingresosPagosE = reportes.totalPagosEfectivoDiario(fechaInicio);
        //ingresos por pagos cobrados con tarjeta
        ingresosPagoT = reportes.totalPagosTarjetaDiario(fechaInicio);
        //ingresos totales diarios en efectivo
        ingresosEfectivo = reportes.ingresoEfectivoCajaDiario(fechaInicio) + reportes.totalPagosEfectivoDiario(fechaInicio) + reportes.ingresoDiarioEfectivo(fechaInicio);
        //ingreso a bancos por ventas con tarjeta y pagos con tarjeta diarios
        Ingresosbancos = reportes.IngresoAbancosDiario(fechaInicio) + reportes.totalPagosTarjetaDiario(fechaInicio);
        //creditos realizados 
        //creditos = reportes.TotalCreditosDiario(fechaInicio)-(reportes.totalPagosEfectivoDiario(fechaInicio) + reportes.totalPagosTarjetaDiario(fechaInicio));
        creditos = reportes.TotalCreditosDiario(fechaInicio);
        //egresos realizados
        egresos = reportes.TotalGastosDiario(fechaInicio);
        //total vendido
        totalVendidio = reportes.IngresosTotalesDiario(fechaInicio);
        //Margen de utilidad
        reportes.setPrecioDolar(precioVentaDolar);
        utilidad = reportes.precioVenta(fechaInicio);
        //existencia real en caja
        exisCaja = (ingresosEfectivo + base) - egresos;
        //ejecutar funcion para obtener los dolares y cordobas recibidos
        reportes.MonedasRecibidas(fechaInicio);
        reportes.EgresoMonedas(fechaInicio);
        //dolares recibidos
        dolaresVendidos = reportes.getDolares() - reportes.getEgresoDolares();
        //cordobas recibidos
        reportes.MonedasRecibidasCompra(fechaInicio);
        dolaresComprados = reportes.getDolaresComprados();
        //dolares convertidos a cordobas
        totalCordobasPorCompraDolar = dolaresComprados * precioCompraDolar;
        totalCordobasPorVentaDolar = dolaresVendidos * precioVentaDolar;
        //difencia en cordobas para el cuadre del reporte diario
        diferenciaEnCordobas = exisCaja - (totalCordobasPorCompraDolar+totalCordobasPorVentaDolar);
        //llenar los lbls
        menu.lblBase.setText("" + this.formato.format(base));
        menu.lblVentasEfectivoDiario.setText("" + this.formato.format(ingresosVentaE));
        menu.lblVentasTarjetaDiario.setText("" + this.formato.format(ingresosVentasT));
        menu.lblIngresosPagosEfectivoDiario.setText("" + this.formato.format(ingresosPagosE));
        menu.lblIngresosPagosTarjetaDiario.setText("" + this.formato.format(ingresosPagoT));
        menu.lblIngresoEfectivo.setText("" + this.formato.format(reportes.ingresoDiarioEfectivo(fechaInicio)));
        menu.lblCreditosDiarios.setText("" + this.formato.format(creditos));
        menu.lblEgresosDiarios.setText("" + this.formato.format(egresos));
        menu.lblTotalExistenciaCajaDiario.setText("" + this.formato.format(exisCaja));
        menu.lblIngresosBancosDiario.setText("" + this.formato.format(Ingresosbancos));
        menu.lbltotalVendidoDiario.setText("" + this.formato.format(totalVendidio));
        menu.lblTotalUtilidadDiario.setText(""+this.formato.format(utilidad));
        menu.lblCantidadCordobas.setText("" + this.formato.format(dolaresComprados));
        menu.lblCantidadDolares.setText("" + this.formato.format(dolaresVendidos));
        menu.lblPrecioCompraDolarEnCordobas.setText(""+precioCompraDolar);
        menu.lblPrecioVentaDolarEnCordobas.setText(""+precioVentaDolar);
        menu.lblTotalCordobasPorCompraDolar.setText(""+this.formato.format(totalCordobasPorCompraDolar));
        menu.lblTotalCordobasPorVentaDolar.setText(""+this.formato.format(totalCordobasPorVentaDolar));
        menu.lblDiferenciaEnCordobas.setText(""+this.formato.format(diferenciaEnCordobas));
    }

    //funcion para filtros de reportes por rangos
    public void SumaTotalFiltroReporte(Date fecha1, Date fecha2) {
        float totalVendidio = 0, exisCaja = 0, ingresosEfectivo = 0, Ingresosbancos = 0, creditos = 0, Egresos = 0;
        long f1 = fecha1.getTime(), f2 = fecha2.getTime();
        java.sql.Date fechaInicio = new java.sql.Date(f1);//convertir la fecha a formato sql
        java.sql.Date fechaFinal = new java.sql.Date(f2);//convertir la fecha a formato sql
        //ventas en efectivo
        menu.lblTotalVentasEfectivoFiltro.setText("" + this.formato.format(reportes.ingresoEfectivoCaja(fechaInicio, fechaFinal)));
        //ingreso de efectivo a caja
        ingresosEfectivo = reportes.ingresoEfectivoCaja(fechaInicio, fechaFinal) + reportes.totalPagosEfectivo(fechaInicio, fechaFinal) + reportes.ingresoEfecitivoRango(fechaInicio, fechaFinal);
        menu.lblIngresosEfectivo.setText("" + this.formato.format(reportes.ingresoEfecitivoRango(fechaInicio, fechaFinal)));
        //ingresos ventas con tarjetas
        menu.lblVentasTarjetaFiltro.setText("" + this.formato.format(reportes.IngresoAbancos(fechaInicio, fechaFinal)));
        //ingreso´por abonos en efectivo
        menu.lblAbonosEfectivoFiltro.setText("" + this.formato.format(reportes.totalPagosEfectivo(fechaInicio, fechaFinal)));
        //ingreso por abonos con tarjeta
        menu.lblAbonosTarjetaFiltro.setText("" + this.formato.format(reportes.totalPagosTarjeta(fechaInicio, fechaFinal)));
        //ingresos a bancos
        Ingresosbancos = reportes.IngresoAbancos(fechaInicio, fechaFinal) + reportes.totalPagosTarjeta(fechaInicio, fechaFinal);
        menu.lblTotalBancosReportFiltro.setText("" + this.formato.format(Ingresosbancos));
        //creditos realizados
        creditos = reportes.TotalCreditosMensual(fechaInicio, fechaFinal) - (reportes.totalPagosEfectivo(fechaInicio, fechaFinal) + reportes.totalPagosTarjeta(fechaInicio, fechaFinal));
        menu.lblCreditosReportFiltro.setText("" + this.formato.format(creditos));
        //salida de efectivo
        Egresos = reportes.TotalGastos(fechaInicio, fechaFinal);
        menu.lblSalidaEfectivoFiltro.setText("" + this.formato.format(Egresos));
        //total vendido
        totalVendidio = reportes.IngresosTotales(fechaInicio, fechaFinal);
        menu.lblTotalVendidoReportFiltro.setText("" + this.formato.format(totalVendidio));
        //total existencia en caja
        exisCaja = ingresosEfectivo - Egresos;
        menu.lblTotalEfectivoCajaFiltro.setText("" + this.formato.format(exisCaja));
    }

    public void ReporteGlobal() {
        float totalVendidio = 0,
		exisCaja = 0,
		ingresosVentasE = 0,
		IngresosVentasT = 0,
		IngresosPagosE = 0,
		IngresosPagosT = 0,
		Ingresosbancos = 0,
		creditos = 0,
		Egresos = 0,
		apertura = 0,
		IngresoEfectivo = 0;
        //aperturas a restar al efectivo en caja
        apertura = reportes.TotalAperturasCajaGlobal() - reportes.PrimeraApertura();
        //ingreso de efectivo a caja
        ingresosVentasE = reportes.ingresoEfectivoCajaGlobal();
        menu.lblIngresosCajaMes.setText("" + this.formato.format(ingresosVentasE));
        //ingresos por ventas con tarjeta
        IngresosVentasT = reportes.IngresoAbancosGlobal();
        menu.lblIngresosVentasTarjetaMes.setText("" + this.formato.format(IngresosVentasT));
        //ingresos por pagos en efectivo
        IngresosPagosE = reportes.totalPagosEfectivoGlobal();
        menu.lblIngresosPagosEfectivoMes.setText("" + this.formato.format(IngresosPagosE));
        //Ingresos por pagos con tarjeta
        IngresosPagosT = reportes.totalPagosTarjetaGlobal();
        menu.lblIngresosPagosTarjetaMes.setText("" + this.formato.format(IngresosPagosT));
        //
        IngresoEfectivo = reportes.TotalIngresoEfectivoGlobal();
        menu.lblIngresoEfectivoGlobal.setText("" + this.formato.format(IngresoEfectivo));
        //ingresos a bancos
        Ingresosbancos = reportes.IngresoAbancosGlobal() + reportes.totalPagosTarjetaGlobal();
        menu.lblIngresosBancoFiltro.setText("" + this.formato.format(Ingresosbancos));
        //creditos realizados
        creditos = reportes.TotalCreditosGlobal() - (reportes.totalPagosEfectivoGlobal() + reportes.totalPagosTarjetaGlobal());
        menu.lblCreditosFiltro.setText("" + this.formato.format(creditos));
        //salida de efectivo
        Egresos = reportes.TotalGastosGlobal();
        menu.lblEgresosFiltro.setText("" + this.formato.format(Egresos));
        //total vendido
        totalVendidio = reportes.IngresosTotalesGlobal();
        menu.lblTotalVendidoFiltro.setText("" + this.formato.format(totalVendidio));
        //total existencia en caja
        exisCaja = (IngresosPagosE + ingresosVentasE + IngresoEfectivo) - Egresos;
        menu.lblExistenciaCajaFiltro.setText("" + this.formato.format(exisCaja));
    }

    public void MostrarReportesDario(Date fecha1)//metodo para llenar la tabla de reortes por rango o mensual del menu reportes
    {
        long f1 = fecha1.getTime();//
        java.sql.Date fechaInicio = new java.sql.Date(f1);//convertir la fecha a formato sql
        /*String totalCreditoMensual = reportes.TotalCreditosMensual(fechaInicio, fechaFinal);//obtengo el valor de total de creditos cn la funcion TotalCreditoMensula de la clase reortes
        String totalGastos = reportes.TotalGastos(fechaInicio, fechaFinal);//total de gastos 
        String totalPagos = reportes.totalPagos(fechaInicio, fechaFinal);//total pagos 
        float creditosPendiente = Float.parseFloat(totalCreditoMensual) - Float.parseFloat(totalPagos);//refleja el creditos menos los pagos*/
        menu.tblReporte.getTableHeader().setFont(new Font("Sugoe UI", Font.PLAIN, 14));
        menu.tblReporte.getTableHeader().setOpaque(false);
        menu.tblReporte.getTableHeader().setBackground(new Color(69, 76, 89));
        menu.tblReporte.getTableHeader().setForeground(new Color(255, 255, 255));
        menu.tblReporte.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 35));
        try {
            menu.tblReporte.setModel(reportes.ReporteDiario(fechaInicio));
            /*menu.lblTotalCreditosFiltroReporte.setText(String.valueOf(creditosPendiente));//lleno el lblTotalCreditoFiltroRepote con el  total creditos 
            menu.lblGastos.setText(totalGastos);//lleno lblGastos con el total de gastos
            menu.lblTotalPagos.setText(totalPagos);*/

        } catch (Exception err) {

        }

    }

    public void MostrarDetalleFactura(int id)//metodo para llenar la tabla que muestra el detalle de las facturas de reportes
    {
        menu.tblMostrarDetalleFactura.getTableHeader().setFont(new Font("Sugoe UI", Font.PLAIN, 14));
        menu.tblMostrarDetalleFactura.getTableHeader().setOpaque(false);
        menu.tblMostrarDetalleFactura.getTableHeader().setBackground(new Color(69, 76, 89));
        menu.tblMostrarDetalleFactura.getTableHeader().setForeground(new Color(255, 255, 255));
        menu.tblMostrarDetalleFactura.setModel(reportes.DetalleFactura(id));
    }

    public void LimpiarAperturas() {
        menu.txtEfectivoApertura.setText("");
    }

    public void LimpiarCierre() {
        menu.txtExistenciaEfectivoCierre.setText("");
        menu.txtBancosCierre.setText("");
        menu.txtCreditosCierre.setText("");
        menu.txtEgresosCierre.setText("");
        menu.txtTotalVendidoCierre.setText("");
        menu.txtDescripcionCierre.setText("");
        menu.txtFechaInicioCierre.setText("");
        menu.txtFechaFinalCierre.setText("");
        menu.txtMesCierre.setText("");
        menu.txtIngresoEfectivoCierre.setText("");
        menu.txtCajaCierre.setText("");
    }

    public void GuardarAperturas() {
        String efectivo = menu.txtEfectivoApertura.getText();
        Date fecha = menu.jcFechaApertura.getDate();
        long fec = fecha.getTime(), fechaActual = this.fecha.getTime();
        java.sql.Date f = new java.sql.Date(fec);
        java.sql.Date f1 = new java.sql.Date(fechaActual);
        int caja = aperturas.IdCaja(menu.cmbCajasApertura.getSelectedItem().toString());
        if (menu.isNumeric(menu.txtEfectivoApertura.getText())) {
            if (!efectivo.equals("")) {
                float efectivoA = Float.parseFloat(efectivo);
                aperturas.GuardarAperturas(f, caja, efectivoA);
                menu.lblBase.setText("" + reportes.baseEfectivoDiario(f1));
                reportesDiarios(f1);
                LimpiarAperturas();
                ReporteGlobal();
            }

        } else {

        }
    }
//funcion pára realizar el corte por los momentos no se esta usando

    public void RealizarCorte() {
        String descripcion = menu.txtDescripcionCierre.getText(), fecha = menu.txtFechaInicioCierre.getText();;
        String efectivo = menu.txtIngresoEfectivoCierre.getText(),
                bancos = menu.txtBancosCierre.getText(),
                creditos = menu.txtCreditosCierre.getText(),
                totalV = menu.txtTotalVendidoCierre.getText(),
                Egresos = menu.txtEgresosCierre.getText(),
                existCaja = menu.txtExistenciaEfectivoCierre.getText(),
                fechaInicio = menu.txtFechaInicioCierre.getText(),
                fechaFinal = menu.txtFechaFinalCierre.getText(),
                mes = menu.txtMesCierre.getText();
        int caja = aperturas.IdCaja(menu.cmbCajasApertura.getSelectedItem().toString());
        if (menu.isNumeric(efectivo) && menu.isNumeric(bancos) && menu.isNumeric(creditos) && menu.isNumeric(totalV) && menu.isNumeric(Egresos) && menu.isNumeric(existCaja)) {
            if (!efectivo.equals("") && !bancos.equals("") && !creditos.equals("") && !totalV.equals("") && !Egresos.equals("") && !existCaja.equals("")) {
                float efectivoC = Float.parseFloat(efectivo),
                        bancosC = Float.parseFloat(bancos),
                        creditosC = Float.parseFloat(creditos),
                        totalVC = Float.parseFloat(totalV),
                        EgresosC = Float.parseFloat(Egresos),
                        ExistenciaCaja = Float.parseFloat(existCaja);
                aperturas.GuardarCierre(fechaInicio, fechaFinal, mes, caja, efectivoC, bancosC, creditosC, EgresosC, totalVC, ExistenciaCaja, descripcion);
                LimpiarCierre();
            }

        } else {

        }
    }

    public void EstiloTablaTotalV() {
        menu.tblMostrarTotalV.getTableHeader().setFont(new Font("Sugoe UI", Font.PLAIN, 14));
        menu.tblMostrarTotalV.getTableHeader().setOpaque(false);
        menu.tblMostrarTotalV.getTableHeader().setBackground(new Color(100, 100, 100));
        menu.tblMostrarTotalV.getTableHeader().setForeground(new Color(255, 255, 255));
    }

    public void mostrarProductosMasVendidios(Date fecha1, Date fecha2) {
        menu.tblProductosMasVendidos.getTableHeader().setFont(new Font("Sugoe UI", Font.PLAIN, 14));
        menu.tblProductosMasVendidos.getTableHeader().setOpaque(false);
        menu.tblProductosMasVendidos.getTableHeader().setBackground(new Color(100, 100, 100));
        menu.tblProductosMasVendidos.getTableHeader().setForeground(new Color(255, 255, 255));
        long f1 = fecha1.getTime(), f2 = fecha2.getTime();
        java.sql.Date ff1 = new java.sql.Date(f1);
        java.sql.Date ff2 = new java.sql.Date(f2);
        menu.tblProductosMasVendidos.setModel(reportes.productosMasVendidos(ff1, ff2));
    }

    public void EnviarCorreoRD() {
        try {
            int nfilas = menu.tblProductosMasVendidos.getRowCount();
            String nombre = "", marca = "", cantidad = "", fila = "";
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM YYYY");
            Properties props = new Properties();
            //servidor de correo
            props.setProperty("mail.smtp.host", "smtp.gmail.com");
            //si va a usar tls en este caso sera true
            props.setProperty("mail.smtp.starttls.enable", "true");
            //cual sera el puerto en este caso gmail usa el puerto 587
            props.setProperty("mail.smtp.port", "587");
            //si se va autenticar en servidor de gmail en este caso si
            props.setProperty("mail.smtp.auth", "true");

            Session sesion = Session.getDefaultInstance(props);
            this.info.obtenerInfoFactura();

            String correoRemitente = "cdsoft00@gmail.com";
            String passwordRemitente = "19199697tsoCD";
            String correoReceptor = menu.txtCorreo.getText();
            String asunto = "Reporte del " + sdf.format(menu.jcFechaReporteDario.getDate());
            String mensaje = "<!DOCTYPE html>\n"
                    + "<html lang=\"en\">\n"
                    + "    <head>\n"
                    + "        <meta charset=\"UTF-8\">\n"
                    + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                    + "        <title></title>\n"
                    + "    </head>\n"
                    + "    <body>\n"
                    + "        <div class=\"col-sm-12\" style=\"width:98%; margin: auto; padding: 0;\">\n"
                    + "            <div class=\"card border-success\" style=\"border:1px solid lime\">\n"
                    + "                <div class=\"card-header text-center bg-dark\" style=\"margin-top: -2%; border-bottom: 1px solid gray; background-color: #383838; text-align: center; padding-top: 1%;\">\n"
                    + "                    <h4 class=\"text-white\" style=\"color: white; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\">" + info.getNombre().toUpperCase() + "</h4>\n"
                    + "                </div>\n"
                    + "                <div class=\"card-body\" style=\"padding: 1%;\">\n"
                    + "                    <h4 class=\"card-title text-center\" style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; text-align: center; color:black\">REPORTE DIARIO</h4>\n"
                    + "                    <table class=\"table table-striped\" style=\"width: 100%; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color:black; border:1px solid #dddddd; padding: 1%; font-size: 14px;\">\n"
                    + "                        <tbody>\n"
                    + "                            <tr style=\"padding: 10%; background-color: aliceblue;\">\n"
                    + "                                <td style=\"padding: 1.5%; text-align: left;\">EFECTIVO APERTURA EN CAJA</td>\n"
                    + "                                <td style=\"text-align: right; padding-right: 3%;\">C$ " + menu.lblBase.getText() + "</td>\n"
                    + "                            </tr>\n"
                    + "                            <tr style=\"padding: 2%;\">\n"
                    + "                                <td style=\"padding: 1.5%; text-align: left;\">INGRESOS POR VENTAS EN EFECTIVO</td>\n"
                    + "                                <td style=\"text-align: right; padding-right: 3%;\">C$ " + menu.lblVentasEfectivoDiario.getText() + "</td>\n"
                    + "                            </tr>\n"
                    + "                            <tr style=\"background-color: aliceblue;\">\n"
                    + "                                <td style=\"padding: 1.5%; text-align: left;\">INGRESOSO POR VENTAS CON TARJETA</td>\n"
                    + "                                <td style=\"text-align: right; padding-right: 3%;\">C$ " + menu.lblVentasTarjetaDiario.getText() + "</td>\n"
                    + "                            </tr>\n"
                    + "                            <tr style=\"background-color: white;\">\n"
                    + "                                <td style=\"padding: 1.5%; text-align: left;\">INGRESOS POR ABONOS EN EFECTIVO</td>\n"
                    + "                                <td style=\"text-align: right; padding-right: 3%;\">C$ " + menu.lblIngresosPagosEfectivoDiario.getText() + "</td>\n"
                    + "                            </tr>\n"
                    + "                            <tr style=\"background-color: aliceblue;\">\n"
                    + "                                <td style=\"padding: 1.5%; text-align: left;\">INGRESOS POR ABONOS CON TARJERTA</td>\n"
                    + "                                <td style=\"text-align: right; padding-right: 3%;\">C$ " + menu.lblIngresosPagosTarjetaDiario.getText() + "</td>\n"
                    + "                            </tr>\n"
                    + "                            <tr style=\"background-color: white;\">\n"
                    + "                                <td style=\"padding: 1.5%; text-align: left;\">INGRESOS DE EFECTIVO</td>\n"
                    + "                                <td style=\"text-align: right; padding-right: 3%;\">C$ " + menu.lblIngresoEfectivo.getText() + "</td>\n"
                    + "                            </tr>\n"
                    + "                            <tr style=\"background-color: aliceblue;\">\n"
                    + "                                <td style=\"padding: 1.5%; text-align: left;\">CREDITOS</td>\n"
                    + "                                <td style=\"text-align: right; padding-right: 3%;\">C$ " + menu.lblCreditosDiarios.getText() + "</td>\n"
                    + "                            </tr>\n"
                    + "                            <tr style=\"background-color: white;\">\n"
                    + "                                <td style=\"padding: 1.5%; text-align: left;\">SALIDA DE AFECTIVO</td>\n"
                    + "                                <td style=\"text-align: right; padding-right: 3%;\">C$ " + menu.lblEgresosDiarios.getText() + "</td>\n"
                    + "                            </tr>\n"
                    + "                            <tr style=\"background-color: #b4f1b5;\">\n"
                    + "                                <td style=\"padding: 1.5%; text-align: left;\">TOTAL VENDIDO</td>\n"
                    + "                                <td style=\"text-align: right; padding-right: 3%;\">C$ " + menu.lbltotalVendidoDiario.getText() + "</td>\n"
                    + "                            </tr>\n"
                    + "                            <tr style=\"background-color: #b4f1b5;\">\n"
                    + "                                <td style=\"padding: 1.5%; text-align: left;\">BANCOS</td>\n"
                    + "                                <td style=\"text-align: right; padding-right: 3%;\">C$ " + menu.lblIngresosBancosDiario.getText() + "</td>\n"
                    + "                            </tr>\n"
                    + "                            <tr style=\"background-color: #b4f1b5;\">\n"
                    + "                                <td style=\"padding: 1.5%; text-align: left;\">TOTAL EXISTENCIA EN CAJA</td>\n"
                    + "                                <td style=\"text-align: right; padding-right: 3%;\">C$ " + menu.lblTotalExistenciaCajaDiario.getText() + "</td>\n"
                    + "                            </tr>\n"
                    + "                        </tbody>\n"
                    + "                    </table>\n"
                    + "                </div>\n"
                    + "            </div>\n"
                    + "        </div>\n"
                    + "        <div class=\"col-sm-12\" style=\"width:98%; margin: auto; padding: 0;\">\n"
                    + "            <div class=\"card border-success\" style=\"border:1px solid lime; border-top: none;\">\n"
                    + "                <div class=\"card-body\" style=\"padding: 1%;\">\n"
                    + "                    <h4 class=\"card-title text-center\" style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; text-align: center;\">PRODUCTOS MAS VENDIDOS</h4>\n"
                    + "                    <table class=\"table table-striped\" style=\"width: 100%; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color:black; border:1px solid #dddddd; padding: 1%; font-size: 14px;\">\n"
                    + "                        <tbody>";
            if (nfilas > 10) {
                nfilas = 10;
            } else {

            }
            for (int i = 0; i < nfilas; i++) {
                nombre = (String) menu.tblProductosMasVendidos.getValueAt(i, 1);
                marca = (String) menu.tblProductosMasVendidos.getValueAt(i, 2);
                cantidad = (String) menu.tblProductosMasVendidos.getValueAt(i, 4);
                mensaje += "<tr style=\"padding: 10%; background-color: aliceblue;\">\n"
                        + "                                <td style=\"padding: 1.5%; text-align: left;\">" + nombre + " " + marca + "</td>\n"
                        + "                                <td style=\"text-align: right; padding-right: 3%;\">" + cantidad + "</td>\n"
                        + "                            </tr>";
            }

            mensaje += "</tbody>\n"
                    + "                    </table>\n"
                    + "                </div>\n"
                    + "            </div>\n"
                    + "        </div>\n"
                    + "    </body>\n"
                    + "</html>";

            MimeMessage message = new MimeMessage(sesion);
            message.setFrom(new InternetAddress(correoRemitente));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(correoReceptor));
            //incluimos el asunto
            message.setSubject(asunto);
            //incluimos el cuerpo osea el mensaje
            message.setText(mensaje, "ISO-8859-1", "html");
            Transport t = sesion.getTransport("smtp");
            t.connect(correoRemitente, passwordRemitente);
            t.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            t.close();

            JOptionPane.showMessageDialog(null, "Correo enviado correctamente.!");
        } catch (AddressException ex) {
            JOptionPane.showMessageDialog(null, ex);
            //Logger.getLogger(envio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            JOptionPane.showMessageDialog(null, ex);
            //Logger.getLogger(envio.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.VK_ENTER == e.getKeyCode()) {
            //busqueda de factura por numero de facturas
            String id = menu.txtBuscarFactura.getText();
            if (menu.isNumeric(id)) {
                Date fecha = menu.jcFacturasEmitidas.getDate();
                long ff1 = fecha.getTime();
                java.sql.Date f = new java.sql.Date(ff1);
                try {
                    menu.tblReporte.setModel(this.reportes.BuscarFactura(Integer.parseInt(id)));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "");
                }
            }

        }
        if (e.VK_SPACE == e.getKeyCode()) {
            //GuardarAperturas();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void ValidarExistApertura() {
        Date fecha = menu.jcFechaApertura.getDate();
        long ff1 = fecha.getTime();
        java.sql.Date f = new java.sql.Date(ff1);
        if (aperturas.ExistenciaApertura(f)) {
            menu.txtEfectivoApertura.setEnabled(false);
            menu.btnGuardarApertura.setEnabled(false);
            menu.lblExistApertura.setText("Ya existe una apertura para esta fecha");
        } else {
            menu.lblExistApertura.setText("");
            menu.txtEfectivoApertura.requestFocus();
        }
    }
}
