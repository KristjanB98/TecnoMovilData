import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AnalisisTransporte {

    // Tarea a)
    public static Map<String, Long> afluenciaPorEstacion(List<Registro> registros) {
        return afluenciaPorEstacion(registros, false);
    }

    public static Map<String, Long> afluenciaPorEstacion(List<Registro> registros, boolean paralelo) {
        Stream<Registro> flujo = paralelo ? registros.parallelStream() : registros.stream();
        return flujo
                .filter(r -> r.accion() == Accion.ENTRADA)
                .collect(Collectors.groupingBy(Registro::estacion, Collectors.counting()));
    }

    // Tarea b)
    public static Map<Integer, Long> flujoPorHora(List<Registro> registros) {
        return registros.stream()
                .filter(r -> r.accion() == Accion.ENTRADA)
                .collect(Collectors.groupingBy(
                        r -> r.timestamp().getHour(),
                        TreeMap::new,
                        Collectors.counting()));
    }

    public static List<Map.Entry<Integer, Long>> horasPico(List<Registro> registros, int cantidad) {
        return topN(flujoPorHora(registros), cantidad);
    }

    // Tarea c)
    public static Map<String, Long> volumenPorRuta(List<Registro> registros) {
        return registros.stream()
                .filter(r -> r.accion() == Accion.ENTRADA)
                .collect(Collectors.groupingBy(Registro::ruta, Collectors.counting()));
    }

    public static List<Map.Entry<String, Long>> rutasMasUsadas(List<Registro> registros, int cantidad) {
        return topN(volumenPorRuta(registros), cantidad);
    }

    // Tarea d)
    public static Map<String, List<String>> patronesDeViaje(List<Registro> registros) {
        return registros.stream()
                .filter(r -> r.accion() == Accion.ENTRADA)
                .sorted(Comparator.comparing(Registro::timestamp))
                .collect(Collectors.groupingBy(
                        Registro::idUsuario,
                        LinkedHashMap::new,
                        Collectors.mapping(Registro::estacion, Collectors.toUnmodifiableList())));
    }

    // Tarea e)
    public static Map<String, Double> tiempoPromedioEntreEstaciones(List<Registro> registros) {
        return registros.stream()
                .filter(r -> r.accion() == Accion.ENTRADA)
                .sorted(Comparator.comparing(Registro::timestamp))
                .collect(Collectors.groupingBy(Registro::idUsuario))
                .values().stream()
                .flatMap(AnalisisTransporte::tramosDelViaje)
                .collect(Collectors.groupingBy(
                        Tramo::etiqueta,
                        TreeMap::new,
                        Collectors.averagingLong(Tramo::minutos)));
    }

    private record Tramo(String origen, String destino, long minutos) {
        String etiqueta() {
            return origen + " -> " + destino;
        }
    }

    private static Stream<Tramo> tramosDelViaje(List<Registro> viaje) {
        return IntStream.range(1, viaje.size())
                .mapToObj(i -> new Tramo(
                        viaje.get(i - 1).estacion(),
                        viaje.get(i).estacion(),
                        Duration.between(viaje.get(i - 1).timestamp(), viaje.get(i).timestamp()).toMinutes()));
    }

    // Tarea f)
    public static Map<String, String> estadoPorRuta(List<Registro> registros, long umbral) {
        return volumenPorRuta(registros).entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> clasificar(e.getValue(), umbral),
                        (a, b) -> a,
                        TreeMap::new));
    }

    public static List<String> rutasCriticas(List<Registro> registros, long umbral) {
        return volumenPorRuta(registros).entrySet().stream()
                .filter(e -> e.getValue() > umbral)
                .map(Map.Entry::getKey)
                .sorted()
                .toList();
    }

    private static String clasificar(long volumen, long umbral) {
        return volumen > umbral ? "CRÍTICA" : "NORMAL";
    }

    // Auxiliar reutilizable (privada)
    private static <K> List<Map.Entry<K, Long>> topN(Map<K, Long> conteos, int cantidad) {
        return conteos.entrySet().stream()
                .sorted(Map.Entry.<K, Long>comparingByValue().reversed())
                .limit(cantidad)
                .toList();
    }
}