use std::fs;
use std::io;
use std::path::{Path, PathBuf};

#[derive(Clone, Debug)]
pub struct Instance {
    pub matrix: Vec<f64>,
    pub n: usize,
    pub a: usize,
    pub b: usize,
}

pub fn load_instance(path: impl AsRef<Path>) -> io::Result<Instance> {
    let content = fs::read_to_string(path)?;
    let mut lines = content.lines();

    let header = lines
        .next()
        .ok_or_else(|| io::Error::new(io::ErrorKind::InvalidData, "missing instance header"))?;
    let mut header_parts = header.split(',').map(str::trim);

    let n = parse_usize(header_parts.next(), "n")?;
    let a = parse_usize(header_parts.next(), "a")?;
    let b = parse_usize(header_parts.next(), "b")?;

    let mut matrix = vec![0.0; n * n];
    for i in 0..n {
        let line = lines.next().ok_or_else(|| {
            io::Error::new(
                io::ErrorKind::InvalidData,
                format!("missing matrix row {i}"),
            )
        })?;
        for (j, value) in line.split_whitespace().enumerate().take(n) {
            matrix[i * n + j] = value.parse::<f64>().map_err(|err| {
                io::Error::new(
                    io::ErrorKind::InvalidData,
                    format!("invalid matrix value at row {i}, column {j}: {err}"),
                )
            })?;
        }
    }

    Ok(Instance { matrix, n, a, b })
}

pub fn csv_instance_files(dir: impl AsRef<Path>) -> io::Result<Vec<PathBuf>> {
    let mut files = Vec::new();
    for entry in fs::read_dir(dir)? {
        let path = entry?.path();
        if path.extension().is_some_and(|ext| ext == "csv") {
            files.push(path);
        }
    }
    files.sort();
    Ok(files)
}

pub fn clean_filename(path: impl AsRef<str>) -> String {
    Path::new(path.as_ref())
        .file_name()
        .and_then(|name| name.to_str())
        .unwrap_or(path.as_ref())
        .to_string()
}

fn parse_usize(value: Option<&str>, name: &str) -> io::Result<usize> {
    value
        .ok_or_else(|| io::Error::new(io::ErrorKind::InvalidData, format!("missing {name}")))?
        .parse::<usize>()
        .map_err(|err| {
            io::Error::new(
                io::ErrorKind::InvalidData,
                format!("invalid {name} in header: {err}"),
            )
        })
}
