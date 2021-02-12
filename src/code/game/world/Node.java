package code.game.world;

import code.engine3d.E3D;
import code.engine3d.Mesh;
import code.math.Culling;
import code.math.Ray;
import code.math.RayCast;
import code.math.Sphere;
import code.math.SphereCast;
import code.math.Vector3D;
import java.util.Vector;

/**
 *
 * @author Roman Lahin
 */
public class Node {
    
    public Mesh mesh;
    public Vector<Node> childs;
    
    public Node(Mesh mesh) {
        this.mesh = mesh;
        childs = new Vector();
    }
    
    public boolean hasChild(Node child) {
        if(child == this) return true;
        
        for(Node node : childs) {
            if(node.hasChild(child)) return true;
        }
        
        return false;
    }
    
    static final Culling cul = new Culling();
    
    public void render(E3D e3d, float[] invCam, World world, long renderTime) {
        mesh.fastIdentityCamera(invCam);
        cul.setBox(mesh.min, mesh.max);
        int visible = cul.visible();
        
        if(visible >= Culling.VISIBLE) {
            mesh.animate(renderTime, true);
            mesh.prepareRender(e3d);
            
            if(visible == Culling.FULLY_VISIBLE) {
                for(int i=0; i<childs.size(); i++) {
                    childs.elementAt(i).renderFully(e3d, invCam, world, renderTime);
                }
            } else {
                for(int i=0; i<childs.size(); i++) {
                    childs.elementAt(i).render(e3d, invCam, world, renderTime);
                }
            }
        }
    }
    
    public void renderFully(E3D e3d, float[] invCam, World world, long renderTime) {
        mesh.fastIdentityCamera(invCam);
        mesh.animate(renderTime, true);
        mesh.prepareRender(e3d);

        for(int i=0; i<childs.size(); i++) {
            childs.elementAt(i).renderFully(e3d, invCam, world, renderTime);
        }
    }
    
    public void sphereCast(Sphere sphere) {
        if(SphereCast.isSphereAABBCollision(
                sphere,
                mesh.min.x, mesh.min.y, mesh.min.z,
                mesh.max.x, mesh.max.y, mesh.max.z)) {
            
            if(mesh.physicsVerts != null) SphereCast.sphereCast(mesh, sphere);
            
            for(int i=0; i<childs.size(); i++) {
                childs.elementAt(i).sphereCast(sphere);
            }
        }
    }
    
    public void rayCast(Ray ray) {
        if(RayCast.isRayAABBCollision(
                ray,
                mesh.min.x, mesh.min.y, mesh.min.z,
                mesh.max.x, mesh.max.y, mesh.max.z)) {
            
            if(mesh.physicsVerts != null) RayCast.rayCast(mesh, ray);
            
            for(int i=0; i<childs.size(); i++) {
                childs.elementAt(i).rayCast(ray);
            }
        }
    }

}
