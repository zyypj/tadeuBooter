package com.github.zyypj.tadeuBooter.minecraft.tool.location.area;

import com.github.zyypj.tadeuBooter.minecraft.tool.location.geographic.SimpleLocation;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Utilitários para criar áreas e formas geométricas a partir de uma Location.
 * <p>
 * Este utilitário retorna coleções de {@link SimpleLocation} representando diferentes formas:
 * esfera, círculo, quadrado, linha, polígono, entre outras.
 */
public final class AreaUtils {

    private AreaUtils() {
        throw new UnsupportedOperationException("Esta classe não pode ser instanciada.");
    }

    /**
     * Retorna as coordenadas que formam uma esfera com centro na {@link SimpleLocation} e raio especificado.
     *
     * @param center A localização central (como SimpleLocation)
     * @param radius O raio da esfera
     * @return Coleção de SimpleLocation representando os blocos dentro da esfera
     */
    public static Collection<SimpleLocation> getSphere(SimpleLocation center, int radius) {
        Collection<SimpleLocation> locations = new ArrayList<>();
        String worldName = center.getWorldName();

        int cx = (int) center.getX();
        int cy = (int) center.getY();
        int cz = (int) center.getZ();
        int radiusSquared = radius * radius;

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int y = cy - radius; y <= cy + radius; y++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    int dx = cx - x;
                    int dy = cy - y;
                    int dz = cz - z;
                    if (dx * dx + dy * dy + dz * dz <= radiusSquared) {
                        locations.add(new SimpleLocation(worldName, x, y, z));
                    }
                }
            }
        }
        return locations;
    }

    /**
     * Retorna as coordenadas que formam um círculo horizontal (no mesmo Y) com centro na Location e raio especificado.
     *
     * @param location A localização central
     * @param radius   O raio do círculo
     * @return Coleção de SimpleLocation representando o círculo
     */
    public static Collection<SimpleLocation> getCircle(Location location, int radius) {
        Collection<SimpleLocation> locations = new ArrayList<>();
        World world = location.getWorld();
        int cx = location.getBlockX();
        int y = location.getBlockY();
        int cz = location.getBlockZ();
        int radiusSquared = radius * radius;

        // Apenas os eixos X e Z (horizontal)
        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                int dx = cx - x;
                int dz = cz - z;
                if (dx * dx + dz * dz <= radiusSquared) {
                    locations.add(new SimpleLocation(world.getName(), x, y, z));
                }
            }
        }
        return locations;
    }

    /**
     * Retorna as coordenadas em formato de "sinal de mais" (cruz horizontal) em volta da Location.
     *
     * @param location A localização central
     * @param radius   A distância máxima a partir do centro
     * @return Coleção de SimpleLocation representando a forma de "+"

     */
    public static Collection<SimpleLocation> getPlusSign(Location location, int radius) {
        Collection<SimpleLocation> locations = new ArrayList<>();
        int cx = location.getBlockX();
        int y = location.getBlockY();
        int cz = location.getBlockZ();

        for (int i = -radius; i <= radius; i++) {
            locations.add(new SimpleLocation(location.getWorld().getName(), cx + i, y, cz));
            locations.add(new SimpleLocation(location.getWorld().getName(), cx, y, cz + i));
        }
        return locations;
    }

    /**
     * Retorna as coordenadas que formam um quadrado com centro na Location e raio especificado.
     *
     * @param location A localização central
     * @param radius   A distância a partir do centro
     * @return Coleção de SimpleLocation representando o quadrado
     */
    public static Collection<SimpleLocation> getSquare(Location location, int radius) {
        Collection<SimpleLocation> locations = new ArrayList<>();
        World world = location.getWorld();
        int cx = location.getBlockX();
        int y = location.getBlockY();
        int cz = location.getBlockZ();

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                locations.add(new SimpleLocation(world.getName(), x, y, z));
            }
        }
        return locations;
    }

    /**
     * Retorna as coordenadas que formam um "star" (diagonais) em volta da Location.
     *
     * @param location A localização central
     * @param radius   O alcance das diagonais
     * @return Coleção de SimpleLocation representando o "star"
     */
    public static Collection<SimpleLocation> getStar(Location location, int radius) {
        Collection<SimpleLocation> locations = new ArrayList<>();
        World world = location.getWorld();
        int cx = location.getBlockX();
        int cz = location.getBlockZ();
        int y = location.getBlockY();

        for (int i = -radius; i <= radius; i++) {
            locations.add(new SimpleLocation(world.getName(), cx + i, y, cz + i));
            locations.add(new SimpleLocation(world.getName(), cx + i, y, cz - i));
        }
        return locations;
    }

    /**
     * Retorna as coordenadas que formam um cubo com centro na Location e raio especificado.
     *
     * @param location A localização central
     * @param radius   A distância a partir do centro em cada eixo
     * @return Coleção de SimpleLocation representando o cubo
     */
    public static Collection<SimpleLocation> getCube(Location location, int radius) {
        Collection<SimpleLocation> locations = new ArrayList<>();
        World world = location.getWorld();
        int cx = location.getBlockX();
        int cy = location.getBlockY();
        int cz = location.getBlockZ();

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int y = cy - radius; y <= cy + radius; y++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    locations.add(new SimpleLocation(world.getName(), x, y, z));
                }
            }
        }
        return locations;
    }

    /**
     * Retorna as coordenadas que formam um triângulo (usando a distância Manhattan) com base na Location.
     *
     * @param location A localização central
     * @param base     O tamanho da base
     * @return Coleção de SimpleLocation representando o triângulo
     */
    public static Collection<SimpleLocation> getTriangle(Location location, int base) {
        Collection<SimpleLocation> locations = new ArrayList<>();
        World world = location.getWorld();
        int cx = location.getBlockX();
        int y = location.getBlockY();
        int cz = location.getBlockZ();

        for (int x = cx - base; x <= cx + base; x++) {
            for (int z = cz - base; z <= cz + base; z++) {
                if (Math.abs(cx - x) + Math.abs(cz - z) <= base) {
                    locations.add(new SimpleLocation(world.getName(), x, y, z));
                }
            }
        }
        return locations;
    }

    /**
     * Retorna as coordenadas que formam uma pirâmide com base na Location.
     *
     * @param location   A localização central da base
     * @param baseLength O tamanho da base da pirâmide
     * @return Coleção de SimpleLocation representando a pirâmide
     */
    public static Collection<SimpleLocation> getPyramid(Location location, int baseLength) {
        Collection<SimpleLocation> locations = new ArrayList<>();
        World world = location.getWorld();
        int cx = location.getBlockX();
        int cy = location.getBlockY();
        int cz = location.getBlockZ();

        // Base
        for (int x = cx - baseLength; x <= cx + baseLength; x++) {
            for (int z = cz - baseLength; z <= cz + baseLength; z++) {
                if (Math.abs(cx - x) + Math.abs(cz - z) <= baseLength) {
                    locations.add(new SimpleLocation(world.getName(), x, cy, z));
                }
            }
        }
        // Camadas superiores
        for (int y = cy + 1; y <= cy + baseLength; y++) {
            for (int x = cx - baseLength; x <= cx + baseLength; x++) {
                for (int z = cz - baseLength; z <= cz + baseLength; z++) {
                    if (Math.abs(cx - x) + Math.abs(cz - z) <= baseLength) {
                        locations.add(new SimpleLocation(world.getName(), x, y, z));
                    }
                }
            }
        }
        return locations;
    }

    /**
     * Retorna as coordenadas que formam um "losango" (diamond) usando a distância Manhattan.
     *
     * @param location A localização central
     * @param radius   O raio (distância máxima)
     * @return Coleção de SimpleLocation representando o losango
     */
    public static Collection<SimpleLocation> getDiamond(Location location, int radius) {
        Collection<SimpleLocation> locations = new ArrayList<>();
        World world = location.getWorld();
        int cx = location.getBlockX();
        int cy = location.getBlockY();
        int cz = location.getBlockZ();

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int y = cy - radius; y <= cy + radius; y++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    if (Math.abs(cx - x) + Math.abs(cy - y) + Math.abs(cz - z) <= radius) {
                        locations.add(new SimpleLocation(world.getName(), x, y, z));
                    }
                }
            }
        }
        return locations;
    }

    /**
     * Retorna as coordenadas formando uma linha vertical a partir da Location.
     *
     * @param location A localização central
     * @param radius   A extensão vertical (para cima e para baixo)
     * @return Coleção de SimpleLocation representando a linha vertical
     */
    public static Collection<SimpleLocation> getVerticalLine(Location location, int radius) {
        Collection<SimpleLocation> locations = new ArrayList<>();
        World world = location.getWorld();
        int x = location.getBlockX();
        int cz = location.getBlockZ();
        int cy = location.getBlockY();

        for (int y = cy - radius; y <= cy + radius; y++) {
            locations.add(new SimpleLocation(world.getName(), x, y, cz));
        }
        return locations;
    }

    /**
     * Retorna as coordenadas formando uma linha horizontal (eixo Z) a partir da Location.
     *
     * @param location A localização central
     * @param radius   A extensão na direção Z
     * @return Coleção de SimpleLocation representando a linha horizontal
     */
    public static Collection<SimpleLocation> getHorizontalLine(Location location, int radius) {
        Collection<SimpleLocation> locations = new ArrayList<>();
        World world = location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int cz = location.getBlockZ();

        for (int z = cz - radius; z <= cz + radius; z++) {
            locations.add(new SimpleLocation(world.getName(), x, y, z));
        }
        return locations;
    }

    /**
     * Retorna as coordenadas formando uma linha entre duas Locations utilizando o algoritmo de Bresenham.
     *
     * @param source      A localização de origem
     * @param destination A localização de destino
     * @return Coleção de SimpleLocation representando a linha
     */
    public static Collection<SimpleLocation> getLine(Location source, Location destination) {
        Collection<SimpleLocation> locations = new ArrayList<>();
        World world = source.getWorld();

        int x1 = source.getBlockX();
        int y1 = source.getBlockY();
        int z1 = source.getBlockZ();
        int x2 = destination.getBlockX();
        int y2 = destination.getBlockY();
        int z2 = destination.getBlockZ();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int dz = Math.abs(z2 - z1);

        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int sz = (z1 < z2) ? 1 : -1;

        int err1 = dx - dy;
        int err2 = dx - dz;

        while (true) {
            locations.add(new SimpleLocation(world.getName(), x1, y1, z1));
            if (x1 == x2 && y1 == y2 && z1 == z2) break;

            int e2 = 2 * err1;
            int e3 = 2 * err2;

            if (e2 > -dy) {
                x1 += sx;
                err1 -= dy;
            }
            if (e2 < dx) {
                y1 += sy;
                err1 += dx;
            }
            if (e3 < dx) {
                z1 += sz;
                err2 += dx;
            }
        }

        return locations;
    }

    /**
     * Conecta os pontos passados formando um polígono fechado.
     *
     * @param points Vetor de Locations (os vértices do polígono)
     * @return Coleção de SimpleLocation representando as conexões entre os pontos
     */
    public static Collection<SimpleLocation> getAbstractConnectedPolygon(Location... points) {
        Collection<SimpleLocation> locations = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            Location current = points[i];
            Location next = points[(i + 1) % points.length];
            locations.addAll(getLine(current, next));
        }
        return locations;
    }

    /**
     * Cria uma conexão "curvada" entre os pontos, simulando um arco.
     *
     * @param locations Vetor de Locations
     * @return Coleção de SimpleLocation representando as conexões curvas
     */
    public static Collection<SimpleLocation> getBentConnections(Location... locations) {
        Collection<SimpleLocation> bentLocations = new ArrayList<>();
        for (int i = 0; i < locations.length; i++) {
            Location source = locations[i];
            Location destination = locations[(i + 1) % locations.length];
            // Aqui, usamos getLine; para um arco, seria necessário interpolar uma curva.
            bentLocations.addAll(getLine(source, destination));
        }
        return bentLocations;
    }

    /**
     * Retorna as coordenadas formando uma linha horizontal ao longo do eixo X.
     *
     * @param location A localização central
     * @param radius   A distância máxima no eixo X
     * @return Coleção de SimpleLocation representando a linha no eixo X
     */
    public static Collection<SimpleLocation> getVerticalX(Location location, int radius) {
        Collection<SimpleLocation> locations = new ArrayList<>();
        World world = location.getWorld();
        int cx = location.getBlockX();
        int y = location.getBlockY();
        int cz = location.getBlockZ();

        for (int i = -radius; i <= radius; i++) {
            locations.add(new SimpleLocation(world.getName(), cx + i, y, cz));
        }
        return locations;
    }

    /**
     * Retorna as coordenadas formando um "X" (diagonais) na horizontal.
     *
     * @param location A localização central
     * @param radius   A distância máxima das diagonais
     * @return Coleção de SimpleLocation representando o "X"
     */
    public static Collection<SimpleLocation> getX(Location location, int radius) {
        Collection<SimpleLocation> locations = new ArrayList<>();
        World world = location.getWorld();
        int cx = location.getBlockX();
        int y = location.getBlockY();
        int cz = location.getBlockZ();

        for (int i = -radius; i <= radius; i++) {
            locations.add(new SimpleLocation(world.getName(), cx + i, y, cz + i));
            locations.add(new SimpleLocation(world.getName(), cx - i, y, cz + i));
        }
        return locations;
    }

    /**
     * Retorna as coordenadas formando uma cruz (horizontal e vertical) a partir da Location.
     *
     * @param location A localização central
     * @param radius   O alcance da cruz
     * @return Coleção de SimpleLocation representando a cruz
     */
    public static Collection<SimpleLocation> getCross(Location location, int radius) {
        Collection<SimpleLocation> locations = new ArrayList<>();
        World world = location.getWorld();
        int cx = location.getBlockX();
        int y = location.getBlockY();
        int cz = location.getBlockZ();

        for (int i = -radius; i <= radius; i++) {
            locations.add(new SimpleLocation(world.getName(), cx + i, y, cz));
            locations.add(new SimpleLocation(world.getName(), cx, y, cz + i));
        }
        return locations;
    }

    /**
     * Retorna as coordenadas formando um "coração" vertical (uma aproximação) a partir da Location.
     *
     * @param location A localização central
     * @param radius   O tamanho do "coração"
     * @return Coleção de SimpleLocation representando o coração
     */
    public static Collection<SimpleLocation> getVerticalHeart(Location location, int radius) {
        Collection<SimpleLocation> locations = new ArrayList<>();
        World world = location.getWorld();
        int cx = location.getBlockX();
        int cy = location.getBlockY();
        int cz = location.getBlockZ();

        // Aproximação simples: dois semicírculos unidos por uma linha central
        for (int y = cy - radius; y <= cy + radius; y++) {
            double angle = Math.acos((double)(cy - y) / radius);
            int offset = (int) Math.round(radius * Math.sin(angle));
            locations.add(new SimpleLocation(world.getName(), cx + offset, y, cz));
            locations.add(new SimpleLocation(world.getName(), cx - offset, y, cz));
        }
        return locations;
    }
}