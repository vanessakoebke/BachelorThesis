use std::collections::HashMap;
use std::env;
use std::fs;
use std::io;
use std::path::{Path, PathBuf};

use strongdis_runtime::io::clean_filename;

fn main() -> io::Result<()> {
    let args: Vec<String> = env::args().collect();
    let repo_root = Path::new(env!("CARGO_MANIFEST_DIR")).join("..");
    let left_path = args
        .get(1)
        .map(PathBuf::from)
        .unwrap_or_else(|| repo_root.join("results/java_results_ejml_nc.csv"));
    let right_path = args
        .get(2)
        .map(PathBuf::from)
        .unwrap_or_else(|| repo_root.join("results/rust_results_MV.csv"));

    let left = read_results(&left_path)?;
    let right = read_results(&right_path)?;

    let mut errors = 0;
    for (file, left_result) in &left {
        match right.get(file) {
            Some(right_result) if right_result == left_result => {}
            Some(right_result) => {
                println!("Fehler in Datei: {file} ({left_result} != {right_result})");
                errors += 1;
            }
            None => {
                println!("Fehlt in Vergleichsdatei: {file}");
                errors += 1;
            }
        }
    }

    if errors == 0 {
        println!("Vergleich abgeschlossen");
    } else {
        println!("Vergleich abgeschlossen: {errors} Abweichung(en)");
    }

    Ok(())
}

fn read_results(path: &Path) -> io::Result<HashMap<String, String>> {
    let content = fs::read_to_string(path)?;
    let mut results = HashMap::new();

    for (line_number, line) in content.lines().enumerate() {
        if line_number == 0 || line.trim().is_empty() {
            continue;
        }

        let parts: Vec<&str> = line.split(',').collect();
        if parts.len() < 3 {
            return Err(io::Error::new(
                io::ErrorKind::InvalidData,
                format!("invalid result row in {}: {line}", path.display()),
            ));
        }

        results.insert(clean_filename(parts[0]), parts[2].trim().to_string());
    }

    Ok(results)
}
