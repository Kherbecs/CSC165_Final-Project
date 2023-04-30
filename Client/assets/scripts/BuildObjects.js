var JavaPackages = new JavaImporter(Packages.org.joml.Matrix4f, Packages.tage.GameObject, Packages.java.lang.Math);

with (JavaPackages) {
    var initAvatarTranslation = (new Matrix4f()).translation(10, 5, 0);
    var initAvatarScale = (new Matrix4f()).scaling(1, 1, 1);
    var initPlayerRotation = (new Matrix4f()).rotationY(java.lang.Math.toRadians(135.0));
}