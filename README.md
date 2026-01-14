# Patch Visualizer

A JavaFX application for visualizing diff and patch files, supporting both Windows and Ubuntu environments.

## Features

- **Compare Files**: Select two files and view their differences side-by-side
- **Import Diff/Patch Files**: Import and visualize `.diff` or `.patch` files (unified diff format)
- **Side-by-Side View**: Clear visualization of file differences with syntax highlighting
- **Cross-Platform**: Works on Windows and Ubuntu

## Requirements

- Java 21 or higher
- Maven 3.6 or higher

## Build

```bash
# Build the project
mvn clean package

# Run the application
mvn javafx:run
```

## Usage

### Compare Files

1. Open the application
2. In the "Compare Files" tab:
   - Click "Browse..." to select the original file
   - Click "Browse..." to select the revised file
   - Click "Compare" to view the differences

### Import Diff/Patch Files

1. Open the application
2. Go to the "Import Diff/Patch" tab
3. Click "Import Diff/Patch File" and select a `.diff` or `.patch` file
4. The unified diff will be visualized with syntax highlighting

### Keyboard Shortcuts

- `Ctrl+O` - Open original file
- `Ctrl+R` - Open revised file
- `Ctrl+I` - Import diff/patch file
- `Ctrl+Q` - Exit application

## Building Native Image (GraalVM)

To build a native executable using GraalVM Native Image:

```bash
mvn clean package -Pnative
```

The native executable will be created in the `target` directory.

## Dependencies

- [JavaFX 21](https://openjfx.io/) - UI framework
- [java-diff-utils](https://github.com/java-diff-utils/java-diff-utils) - Diff generation library
- [diff2html](https://diff2html.xyz/) - HTML diff visualization (offline resources included)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
