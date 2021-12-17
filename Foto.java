package dmitrieva_ist18;

public class Foto {
    String foto;
    Inf inf;
    
    Foto(String foto, String name, String info, String date, String kat){
        this.foto = foto;
        inf = new Inf(name, info, date, kat);
    }
    String displayfoto(){
        return foto;
    }
        
    public class Inf{
        String name;
        String info;
        String date;
        String kat;
        
        Inf(String name, String info, String date, String kat) {
            this.name = name;
            this.info = info;
            this.date =date;
            this.kat = kat;
        }
        String displayname(){
            return name;
        }
        String displayinf(){
            return info;
        }
        String displaydate(){
            return date;
        }
        String displaykat(){
            return kat;
        }
        
    }
}
