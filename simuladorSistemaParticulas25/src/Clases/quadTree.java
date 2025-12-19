package Clases;

import javafx.geometry.Point2D;

public class quadTree {
    private regionQuad region;  //la region que representa el quadtree
    private particle particulaAlmacenada; //particula en la region, si esq la region es una hoja
    private double masaTotal;   //masa total dentro de la region
    private double centroMasaX, centroMasaY; //centro de masa de la region
    private boolean isDivided;//booleano que comprueba si es una hoja o no, lo mismo que decir que esta subdividido
    
    private quadTree NE,NW,SW,SE;
    
    public quadTree (regionQuad region ){
        this.region = region;
        this.particulaAlmacenada = null;
        this.masaTotal = 0;
        this.centroMasaX = 0;
        this.centroMasaY = 0;
        this.isDivided = false; 
    }
    //metodo principal insertar
    public boolean insertar(particle particula){
        //verificar que la paritcula este dentro
        if ( !region.contiene( particula.getPosicion() ) ){
            return false;
        }
        if (particulaAlmacenada == null &&  !isDivided){
            particulaAlmacenada = particula;
            actualizarPropiedades(particula);
            return true;
        }
        if (!isDivided){
            subdividir();   
        }
        if (particulaAlmacenada != null) {
            particle cuerpo = particulaAlmacenada;
            particulaAlmacenada = null;
            insertarEnCuadrante(cuerpo);
        }
        return insertarEnCuadrante(particula);
    }
    //subdividir quadTree usando region.dividirRegion()
    private void subdividir(){
        regionQuad[] cuadrantes = region.dividirRegion();
        NE = new quadTree(cuadrantes[0]);
        NW = new quadTree(cuadrantes[1]);
        SW = new quadTree(cuadrantes[2]);
        SE = new quadTree(cuadrantes[3]);
        isDivided = true;
    }
    //comprobar si la particula pertenece a algun cuadrante y meterlo en ese cuadrante
    private boolean insertarEnCuadrante(particle particula){
    if (NE != null && NE.insertar(particula)) return true;
    if (NW != null && NW.insertar(particula)) return true;
    if (SW != null && SW.insertar(particula)) return true;
    if (SE != null && SE.insertar(particula)) return true;
    return false;
}
    //actualizar los valores de quadTree con los de las particulas dentro
    public void actualizarPropiedades(particle particula){
        if (masaTotal == 0){
            masaTotal = particula.getMass();
            centroMasaX  = particula.getPosicion().getX();
            centroMasaY  = particula.getPosicion().getY();
        }else{
            double nuevaMasaTotal = masaTotal + particula.getMass();
            centroMasaX = (centroMasaX*masaTotal + particula.getPosicion().getX()*particula.getMass())/nuevaMasaTotal; 
            centroMasaY = (centroMasaY*masaTotal + particula.getPosicion().getY()*particula.getMass())/nuevaMasaTotal;
            masaTotal = nuevaMasaTotal;
        }
    }
    public void calcularFuerzas(particle objetivo, double theta, double G, double softening){
    if (particulaAlmacenada == null && !isDivided){
        return; // nodo vacío no se hace nada
    }
    
    //calcular distancia al centro de masa
    double dx = centroMasaX - objetivo.getPosicion().getX();
    double dy = centroMasaY - objetivo.getPosicion().getY();
    double distancia = Math.sqrt(dx*dx + dy*dy);
    
    // si es una hoja o el nodo está suficientemente lejano
    if(!isDivided || (region.width/distancia < theta)) {
        if (particulaAlmacenada != objetivo && particulaAlmacenada != null){
            objetivo.addGravitacionalForce(particulaAlmacenada, G, softening); // ← AGREGAR SOFTENING
        }else if (particulaAlmacenada == null && masaTotal > 0){
            particle pseudoParticula = new particle(masaTotal,new Point2D(centroMasaX, centroMasaY), new Point2D(0,0));
            objetivo.addGravitacionalForce(pseudoParticula, G, softening); // ← AGREGAR SOFTENING
        }
    }else {
        //recursivamente visitar hijos
        if (NW != null){NW.calcularFuerzas(objetivo, theta, G, softening);}
        if (NE != null){NE.calcularFuerzas(objetivo, theta, G, softening);}
        if (SE != null){SE.calcularFuerzas(objetivo, theta, G, softening);}
        if (SW != null){SW.calcularFuerzas(objetivo, theta, G, softening);}
    }
}
}
