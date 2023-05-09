var JavaPackages = new JavaImporter(Packages.tage.shapes.Line, Packages.org.joml.Vector3f, Packages.tage.shapes.TerrainPlane);

with (JavaPackages) {
	var linxS = new Line(new Vector3f(0, 5, 0), new Vector3f(3, 0, 0));
	var linyS = new Line(new Vector3f(0, 5, 0), new Vector3f(0, 3, 0));
	var linzS = new Line(new Vector3f(0, 5, 0), new Vector3f(0, 0, 3));
}