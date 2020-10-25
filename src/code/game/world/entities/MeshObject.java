package code.game.world.entities;

import code.engine3d.E3D;
import code.engine3d.Mesh;
import code.engine3d.Renderable;
import code.game.world.World;
import code.math.Ray;
import code.math.RayCast;
import code.math.SphereCast;
import code.math.Vector3D;

/**
 *
 * @author Roman Lahin
 */
public class MeshObject extends PhysEntity {
    
    public Mesh mesh;
    public boolean preciseCollision = true;
    
    public MeshObject(Mesh[] meshes) {
        this.mesh = meshes[0];
        setSize(Math.max(Math.max(mesh.max.z,-mesh.min.z), Math.max(mesh.max.x,-mesh.min.x)), Math.max(0, mesh.max.y));
    }
    
    public void physicsUpdate(World world) {
        super.physicsUpdate(world);
        
        Renderable.buildMatrix(pos, new Vector3D(0, rotY, 0), Renderable.tmpMat.identity()).get(mesh.modelMatrix);
    }
    
    public boolean rayCast(Ray ray, boolean onlyMeshes) {
        if(!preciseCollision) return super.rayCast(ray, onlyMeshes);
        
        if(RayCast.isRayAABBCollision(ray, 
                mesh.min.x, mesh.min.y,  mesh.min.z, 
                mesh.max.x, mesh.max.y,  mesh.max.z)) {
            RayCast.rayCast(mesh, mesh.modelMatrix, ray);
            
            if(ray.mesh == mesh) return true;
        }
        
        return false;
    }
    
    public boolean meshSphereCast(Vector3D sphere, float radius) {
        if(!preciseCollision) return false;
        
        if(SphereCast.isSphereAABBCollision(sphere, radius, 
                mesh.min.x, mesh.min.y,  mesh.min.z, 
                mesh.max.x, mesh.max.y,  mesh.max.z)) {
            return SphereCast.sphereCast(mesh, mesh.modelMatrix, sphere, radius);
        }
        
        return false;
    }
    
    public void render(E3D e3d, World world) {
        mesh.setMatrix(mesh.modelMatrix, world.m, e3d.invCam);
        mesh.prepareRender(e3d);
    }

}
