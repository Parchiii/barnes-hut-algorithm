package Controladores;

import java.io.IOException;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.util.Duration;
import Clases.barnesHutSimulation;
import Clases.particle;
import Clases.chargedParticle;
import Clases.ParticleConfig;
import java.util.Random;

public class MainWindowsControlador implements Initializable {
    
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1055;
    private int[] pixels;
    private PixelBuffer<IntBuffer> pixelBuffer;
    private barnesHutSimulation bhSimulation;
    private ParticleConfig configuracionActual = new ParticleConfig();
    private List<particle> particulas = new ArrayList<>();
    private static final int BACKGROUND = 0xFF000000;
    private static final int PARTICLE_COLOR = 0xFFFF0000;
    
    
    
    @FXML
    private TextField fpstxt;
    @FXML
    private ToggleButton menuToggle;
    @FXML
    private TabPane floatingTabPane;
    private boolean menuVisible = true;
    @FXML
    private ImageView imageView;
    @FXML
    private Button menuconfigToggle;
    @FXML
    private Button reiniciarParticulas;

    //variables para deltatime
    private long tiempoAnterior = 0;
    private double deltaTimeAcumulado = 0;
    private int frameCount = 0;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupPixelBuffer();
        
         // Inicializar simulación Barnes-Hut con parámetros suaves
        bhSimulation = new barnesHutSimulation(500, 0.7, WIDTH, HEIGHT);
        bhSimulation.setSoftening(20.0);
        bhSimulation.setG(500); // Gravedad más suave
        
        //particulas de inicio
        inicializarParticulasAvanzado(200,10,1000,0,0);
        
        startAnimation();
        
        // Configurar el comportamiento del ToggleButton
        menuToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                mostrarMenu();
            } else {
                ocultarMenu();
            }
        });
        // Opcional: Iniciar con el menú oculto
        floatingTabPane.setVisible(false);
        floatingTabPane.setManaged(false);
    }
    private void setupPixelBuffer(){
        pixels = new int[WIDTH * HEIGHT];
        IntBuffer intbuffer = IntBuffer.wrap(pixels);
        
        pixelBuffer = new PixelBuffer<>(
                WIDTH,
                HEIGHT,
                intbuffer,
                PixelFormat.getIntArgbPreInstance()
        );
        
        WritableImage imagen = new WritableImage(pixelBuffer);
        imageView.setImage(imagen);
    }
    private void startAnimation(){
        new AnimationTimer(){
            @Override
            public void handle(long now){
                double deltaTime = calcularDeltaTime(now);
                bhSimulation.simulateStep(deltaTime);
                renderFrame();
                mostrarFPS(deltaTime);
            }
        }.start();
    }
    private double calcularDeltaTime(long now){
        if (tiempoAnterior == 0){
            tiempoAnterior = now;
            return 0.016;
        }
        double deltaTime = (now - tiempoAnterior)/1_000_000_000.0;
        tiempoAnterior = now;
        return Math.min(deltaTime, 01);
    }
    private void updateParticle(double deltaTime){
        for (particle p : particulas){
            p.actualizar(deltaTime);
        }
    }
    private void renderFrame(){
        //limpiar frame anterior
        java.util.Arrays.fill(pixels, BACKGROUND);
        //dibujar las particulas
        for (particle p :bhSimulation.getParticulas()){
            dibujarParticula(p);
        }
        pixelBuffer.updateBuffer(pixelbuffer -> null);
    }
    private void dibujarParticula(particle p) {
        int x = (int) p.getPosicion().getX();
        int y = (int) p.getPosicion().getY();

        // Dibujar partícula como un punto 3x3 píxeles
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int px = x + dx;
                int py = y + dy;
                if (px >= 0 && px < WIDTH && py >= 0 && py < HEIGHT) {
                    // Color diferente para partículas cargadas
                    if (p instanceof chargedParticle) {
                        pixels[py * WIDTH + px] = 0xFF00FF00; // VERDE para cargadas
                    } else {
                        pixels[py * WIDTH + px] = 0xFFFF0000; // ROJO para normales
                    }
                }
            }
        }
    }
    private void mostrarFPS(double deltaTime){
        deltaTimeAcumulado += deltaTime;
        frameCount++;
        if (deltaTimeAcumulado >= 0.5) {
            double fps = frameCount/deltaTimeAcumulado;
            int intFps = (int) fps;
            String texto = "FPS: " + intFps + ", Partículas: " + bhSimulation.getParticleCount();
            fpstxt.setText(texto);
            deltaTimeAcumulado = 0;
            frameCount = 0;
        }
    }
    private void inicializarParticulasAvanzado(int cantidad, double masaMin, double masaMax, double cargaMin, double cargaMax) {
        bhSimulation.clearParticles();
        Random random = new Random();
        
        for (int i = 0; i < cantidad; i++) {
            double masa = masaMin + (masaMax - masaMin) * random.nextDouble();
            double carga = cargaMin + (cargaMax - cargaMin) * random.nextDouble();
            
            if (Math.abs(carga) > 0.1) { // Si tiene carga significativa
                chargedParticle nuevaParticula = new chargedParticle(masa, carga);
                bhSimulation.addBody(nuevaParticula);
            } else {
                particle nuevaParticula = new particle(masa);
                bhSimulation.addBody(nuevaParticula);
            }
        }
        
        System.out.println("Inicializadas " + cantidad + " partículas con masa[" + 
                          masaMin + "-" + masaMax + "], carga[" + cargaMin + "-" + cargaMax + "]");
    }
    @FXML
    private void agregarParticula() {}
    @FXML
    private void reiniciarParticulas() {
        bhSimulation.clearParticles();
        System.out.println("Partículas reiniciadas");
    }
    private void mostrarMenu() {
        floatingTabPane.setManaged(true);
        floatingTabPane.setVisible(true);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), floatingTabPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    private void ocultarMenu() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), floatingTabPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            floatingTabPane.setVisible(false);
            floatingTabPane.setManaged(false);
        });
        fadeOut.play();
    }
    public ParticleConfig getConfiguracionActual(){return configuracionActual;} 
    public void actualizarConfiguracion(ParticleConfig nuevaConfig) {
        this.configuracionActual = nuevaConfig;
        System.out.println("Configuración actualizada - Carga: " + configuracionActual.getCarga() + 
                          ", Masa: " + configuracionActual.getMass());
    }
    @FXML
    public void abrirVentanaConfiguracion() throws IOException{
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/Vista/ventanaConfiguracion.fxml"));
            Parent root = loader.load();
            
            VentanaConfiguracionControlador configController = loader.getController();
            configController.setMainController(this);
            
            Stage ventanaConfig = new Stage();
            ventanaConfig.setTitle("Configuracion de Particula");
            Scene scene = new Scene(root);
            ventanaConfig.setScene(scene);
            ventanaConfig.show();
            
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    public void crearParticulaCargadaPreConfig() {
        chargedParticle nuevaParticula = new chargedParticle(
            configuracionActual.getMass(), 
            configuracionActual.getCarga()
        );
        bhSimulation.addBody(nuevaParticula);
        System.out.println("Partícula cargada creada. Carga: " + configuracionActual.getCarga() + 
                          ", Masa: " + configuracionActual.getMass() +
                          ". Total: " + bhSimulation.getParticleCount());
    }
    public void crearParticulaPreConfig() {
        particle nuevaParticula = new particle(configuracionActual.getMass());
        
        bhSimulation.addBody(nuevaParticula);
        System.out.println("Partícula cargada creada. Masa: " + configuracionActual.getMass() +
                          ". Total: " + bhSimulation.getParticleCount());
    }
}
