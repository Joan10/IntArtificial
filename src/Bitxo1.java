package agents;

// Exemple de Bitxo

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;




public class Bitxo1 extends Agent
{
    static final boolean DEBUG = false;
    
    boolean carril_trobat = false;
    boolean jahedisparat = false;
    
    boolean replega=false; //Variable que serveix per comptar els passos de replegament del bitxo.
    long aux_tmp=0; //Variable que serveix per comptar temps
    final int MEITAT = 400;
    
    final int PARET = 0;
    final int NAU   = 1;
    final int RES   = -1;

    final int ESQUERRA = 0;
    final int CENTRAL  = 1;
    final int DRETA    = 2;
    final int DISTANCIA_MINIMA = 45;

    Estat estat;
    int espera = 0;

    long temps;
    
 public class distArray{
        //Classe que manté una llista ordenada del recurs més proper amb el seu índex a la taula.
     
        List<Long> distList = new ArrayList<Long>();
        List<Integer> indexList = new ArrayList<Integer>();
        
        
        public distArray(){
           
        }
        public void fica_element( long dist, int pos ){
            
            long dist_act;
            int i = 0;
            boolean found = false;
            
            if (distList.size() == 0) {
                
                distList.add(dist);
                indexList.add(pos);              
            }else{
                for (i = 0; i<distList.size() && !found; i++){
                    dist_act = distList.get(i);
                    
                    if (dist < dist_act){
                        distList.add(i, dist);
                        indexList.add(i, pos);
                        
                        found = true;
                    }
                }
                if (!found){
                    distList.add(i, dist);
                    indexList.add(i, pos);
                }
            }
            
        }
        
        public int treuElement(int i){
            return indexList.get(i);
        }
                       
        
    }

    
    private void miraBonificacio(){
        //Funció que retorna el sector on es troba la bonificació més propera
        
        int i,mini = 0;
        double dist,mindist = 0;
        int distAnt;
        distArray da = new distArray();
        
        
        
        for (i=0; i<estat.bonificacions.length; i++){
            dist = estat.posicio.distancia(estat.bonificacions[i]);
            da.fica_element((int)dist, i);
        }
        

        distAnt = estat.distanciaVisor;
        setDistanciaVisors((int)estat.bonificacions[da.treuElement(0)].distancia(estat.posicio));
        mira(estat.bonificacions[da.treuElement(0)].x,estat.bonificacions[da.treuElement(0)].y);        
        System.out.println(estat.objecteVisor[1]);
        //if (estat.objecteVisor[1] != 0){
           
        //}
        setDistanciaVisors(distAnt);
        
        
    }

    public Bitxo1(Agents pare) {
        super(pare, "Bitxo1", "imatges/robotank2.gif");
    }

    @Override
    public void inicia()
    {
        setAngleVisors(30);
        setDistanciaVisors(350);
        setVelocitatLineal(5);
        setVelocitatAngular(5);
        replega = false;
        espera = 0;
        temps = 0;
    }

    @Override
    public void avaluaComportament()
    {
        temps++;
        estat = estatCombat();

        
       /* if (temps == 1 && !estat.veigEnemic){  
            //Giram tot d'una a l'esquerra per trobar l'enemic
            if (estat.posicio.x < MEITAT){
                gira(-90);
            }else{
                gira(90);
            }
        }else if(temps > 1 && !estat.veigEnemic && !carril_trobat){
            setAngleVisors(0);          
            if (estat.interseccioVisor[ESQUERRA].x == 31 && estat.interseccioVisor[CENTRAL].x == 31 && estat.interseccioVisor[DRETA].x == 31){
                System.out.println("Trobam paret interior");
                carril_trobat=true;
                endavant();
            }else if (estat.interseccioVisor[ESQUERRA].y == 31 && estat.interseccioVisor[CENTRAL].y == 31 && estat.interseccioVisor[DRETA].y == 31){
                System.out.println("Trobam paret interior");
                carril_trobat=true;
                endavant();
                
            }else if (estat.interseccioVisor[ESQUERRA].y == 569 && estat.interseccioVisor[CENTRAL].y == 569 && estat.interseccioVisor[DRETA].y == 569){
                System.out.println("Trobam paret interior");

                carril_trobat=true;
                endavant();
            }else{
                System.out.println("No trobam la paret: giram");
                gira(90);
            }
            
        }*/
                
        if (espera > 0) {
            if (DEBUG) System.out.println("Espera "+espera);
            espera--;
        }
        else if (replega && temps - aux_tmp > 10){
            gira(-90);
            aux_tmp=temps;
        }
        else if (replega){
            endavant();
            replega = false;
        }
        else
        {
            atura();
            if (estat.enCollisio) // situació de nau bloquejada
            {
                // si veu la nau, dispara
                if (estat.objecteVisor[CENTRAL] == NAU && estat.impactesRival < 5)
                {
                    dispara();   //bloqueig per nau, no giris dispara
                }
                else // hi ha un obstacle, gira i parteix
                {
                    gira(20); // 20 graus
                    if (hiHaParedDavant(20)) enrere();
                    else endavant();
                    espera=8;
                }
            } 
            else {
                endavant();
                if (estat.veigEnemic)
                {
                    setVelocitatLineal(5);
                    setVelocitatAngular(5);
                    if (estat.sector == 2 || estat.sector == 3)
                    {
                        mira(estat.posicioEnemic.x, estat.posicioEnemic.y);
                        
                        if (estat.balaEnemigaDetectada){
                            //Si l'enemic ens dispara
                            System.out.println("bala enemiga!");
                            if (estat.impactesRebuts == 4) {
                                hyperespai();
                            }
                            if (!jahedisparat){
                                //Si l'enemic dispara primer partim!
                                System.out.println("Pirat!!");
                                replega = true;
                                gira(90);
                                endavant();
                                aux_tmp = temps;
                            }
                        }
                        
                        if (estat.impactesRival < 5){
                            jahedisparat = true;
                            dispara();
                        }
                    }
                    else if (estat.sector == 1)
                    {
                        dreta();
                    }
                    else
                    {
                        esquerra();
                    }
                }else{
                    // setVelocitatLineal(2);
                     //setVelocitatAngular(2);
                     jahedisparat = false;
                     miraBonificacio();

                }

                // Miram els visors per detectar els obstacles
                int sensor = 0;

                if (estat.objecteVisor[DRETA] == PARET && estat.distanciaVisors[DRETA] < DISTANCIA_MINIMA) {
                    sensor += 1;
                }
                if (estat.objecteVisor[CENTRAL] == PARET && estat.distanciaVisors[CENTRAL] < DISTANCIA_MINIMA) {
                    sensor += 2;
                }
                if (estat.objecteVisor[ESQUERRA] == PARET && estat.distanciaVisors[ESQUERRA] < DISTANCIA_MINIMA) {
                    sensor += 4;
                }
                if (DEBUG) System.out.println("Sensor: " + sensor);
                
                switch (sensor) {
                    case 0: endavant();
                        break;
                    case 1: // dreta bloquejada
                    case 3: esquerra();                        
                        break;
                    case 4: // esquerra bloquejada
                    case 6: dreta();                        
                        break;
                    case 5: // centre lliure
                        endavant();
                        break;  
                    case 2:  // paret devant
                    case 7:  // si estic molt aprop, torna enrere
                        double distancia;
                        distancia = minimaDistanciaVisors();

                        if (distancia < 15) {
                            espera = 8;
                            enrere();
                        } else // gira aleatòriament a la dreta o a l'esquerra
                            dreta();
                        break;
                }
            }
        }
    }

    boolean hiHaParedDavant(int dist)
    {

       if (estat.objecteVisor[ESQUERRA]== PARET && estat.distanciaVisors[ESQUERRA]<=dist)
           return true;

       if (estat.objecteVisor[CENTRAL ]== PARET && estat.distanciaVisors[CENTRAL ]<=dist)
           return true;

       if (estat.objecteVisor[DRETA   ]== PARET && estat.distanciaVisors[DRETA   ]<=dist)
           return true;
       
       return false;
    }

    double minimaDistanciaVisors()
    {
        double minim;

        minim = Double.POSITIVE_INFINITY;
        if (estat.objecteVisor[ESQUERRA] == PARET)
            minim = estat.distanciaVisors[ESQUERRA];
        if (estat.objecteVisor[CENTRAL] == PARET && estat.distanciaVisors[CENTRAL]<minim)
            minim = estat.distanciaVisors[CENTRAL];
        if (estat.objecteVisor[DRETA] == PARET && estat.distanciaVisors[DRETA]<minim)
            minim = estat.distanciaVisors[DRETA];
        return minim;
    }
}