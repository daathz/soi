'use strict';
Object.defineProperty(exports, "__esModule", { value: true });
const express = require("express");
const movie_1 = require("../schemas/movie");
const router = express.Router();
function generate_id(movieList) {
    let id = 1;
    if (movieList.length) {
        movieList.forEach(movie => {
            if (id <= movie.id) {
                id = movie.id + 1;
            }
        });
    }
    return id;
}
router.get('/find', (req, res) => {
    let year = parseInt(req.query.year);
    if (req.query.orderby === 'Title' || req.query.orderby === 'Director')
        movie_1.Movie.find({ year: year }, 'id', { sort: req.query.orderby.toLowerCase() }, (err, docs) => {
            if (err) {
                res.status(500).json({ info: 'Error executing query.', error: err });
            }
            if (docs) {
                let movieIdList = {
                    id: docs.map(movie => movie.id)
                };
                res.json(movieIdList);
            }
        });
});
router.get('/', (req, res) => {
    movie_1.Movie.find({}, (err, docs) => {
        if (err) {
            res.status(500).json({ info: 'Error executing query.', error: err });
        }
        if (docs) {
            let movieList = {
                movie: docs.map(doc => {
                    let item = {
                        title: doc.title,
                        year: doc.year,
                        director: doc.director,
                        actor: doc.actor
                    };
                    return item;
                })
            };
            res.json(movieList);
        }
    });
});
router.get('/:id', (req, res) => {
    movie_1.Movie.findOne({ id: req.params.id }, (err, doc) => {
        if (err) {
            res.status(500).json({ info: 'Error executing query.', error: err });
        }
        if (doc) {
            let movie = {
                title: doc.title,
                year: doc.year,
                director: doc.director,
                actor: doc.actor
            };
            res.json(movie);
        }
        else {
            res.status(404).end();
        }
    });
});
router.post('/', (req, res) => {
    movie_1.Movie.find({}, (err, movies) => {
        let movie = {
            title: req.body.title,
            year: req.body.year,
            director: req.body.director,
            actor: req.body.actor,
            id: generate_id(movies)
        };
        movie_1.Movie.insertMany([movie], (err, docs) => {
            if (err) {
                res.status(500).json({ info: 'Error executing query.', error: err });
            }
            if (docs) {
                let movieId = {
                    id: movie.id
                };
                res.json(movieId);
            }
        });
    });
});
router.put('/:id', (req, res) => {
    let actors = [];
    if (req.body.actor) {
        actors = req.body.actor;
    }
    let movie = {
        id: parseInt(req.params.id),
        title: req.body.title,
        year: req.body.year,
        director: req.body.director,
        actor: actors
    };
    movie_1.Movie.findOneAndUpdate({ id: req.params.id }, movie, { upsert: true, new: true, runValidators: true }, (err, doc) => {
        if (err) {
            res.status(500).json({ info: 'Error executing query.', error: err });
        }
        if (doc) {
            res.status(200).end();
        }
    });
});
router.delete('/:id', (req, res) => {
    movie_1.Movie.remove({ id: req.params.id }, (err) => {
        if (err) {
            res.status(500).json({ info: 'Error executing query.', error: err });
        }
        res.status(200).end();
    });
});
exports.default = router;
//# sourceMappingURL=movie_service.js.map