var JavaPackages = new JavaImporter(Packages.org.joml.Matrix4f, Packages.tage.GameObject, Packages.java.lang.Math);

with (JavaPackages) {
    var initAvatarTranslation = (new Matrix4f()).translation(10, 5, 0);
    var initAvatarScale = (new Matrix4f()).scaling(.5, .5, .5);
    var initPlayerRotation = (new Matrix4f()).rotationY(java.lang.Math.toRadians(180.0));
}