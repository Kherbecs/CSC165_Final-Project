var JavaPackages = new JavaImporter(Packages.tage.Light, Packages.org.joml.Vector3f);

with (JavaPackages) {
	var light = new Light();
	light.setLocation(new Vector3f(0.0, 50.0, 0.0));
}