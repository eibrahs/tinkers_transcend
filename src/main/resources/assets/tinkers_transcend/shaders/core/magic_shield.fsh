#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform float GameTime;
uniform int EndPortalLayers;
uniform vec4 BackgroundColor;
uniform vec2 Resolution;

in vec4 texProj0;

const vec3[] COLORS = vec3[](
    vec3(0.47, 0.74, 0.84),
    vec3(0.011892, 0.095924, 0.089485),
    vec3(0.027636, 0.101689, 0.100326),
    vec3(0.046564, 0.109883, 0.114838),
    vec3(0.064901, 0.117696, 0.097189),
    vec3(0.063761, 0.086895, 0.123646),
    vec3(0.084817, 0.111994, 0.166380),
    vec3(0.097489, 0.154120, 0.091064),
    vec3(0.106152, 0.131144, 0.195191),
    vec3(0.097721, 0.110188, 0.187229),
    vec3(0.133516, 0.138278, 0.148582),
    vec3(0.070006, 0.243332, 0.235792),
    vec3(0.196766, 0.142899, 0.214696),
    vec3(0.047281, 0.315338, 0.321970),
    vec3(0.204675, 0.390010, 0.302066),
    vec3(0.080955, 0.314821, 0.661491)
);

const mat4 SCALE_TRANSLATE = mat4(
    0.5, 0.0, 0.0, 0.25,
    0.0, 0.5, 0.0, 0.25,
    0.0, 0.0, 1.0, 0.0,
    0.0, 0.0, 0.0, 1.0
);

mat4 end_portal_layer(float layer) {
    mat4 translate = mat4(
        1.0, 0.0, 0.0, 17.0 / layer,
        0.0, 1.0, 0.0, (2.0 + layer / 1.5) * (GameTime * -15),
        0.0, 0.0, 1.0, 0.0,
        0.0, 0.0, 0.0, 1.0
    );

    mat2 rotate = mat2_rotate_z(radians((layer * layer * 4321.0 + layer * 9.0) * 2.0));

    mat2 scale = mat2((4.5 - layer / 4.0) * 0.5);

    return mat4(scale * rotate) * translate * SCALE_TRANSLATE;
}

out vec4 fragColor;
vec3 mod289(vec3 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec2 mod289(vec2 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec3 permute(vec3 x) { return mod289(((x*34.0)+1.0)*x); }

float snoise(vec2 v) {
    const vec4 C = vec4(0.211324865405187,
                        0.366025403784439,
                       -0.577350269189626,
                        0.024390243902439);
    vec2 i  = floor(v + dot(v, C.yy));
    vec2 x0 = v -   i + dot(i, C.xx);
    vec2 i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
    vec4 x12 = x0.xyxy + C.xxzz;
    x12.xy -= i1;
    i = mod289(i);
    vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
                    + i.x + vec3(0.0, i1.x, 1.0 ));
    vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
    m = m*m; m = m*m;
    vec3 x = 2.0 * fract(p * C.www) - 1.0;
    vec3 h = abs(x) - 0.5;
    vec3 ox = floor(x + 0.5);
    vec3 a0 = x - ox;
    m *= 1.79284291400159 - 0.85373472095314 * (a0*a0 + h*h);
    vec3 g;
    g.x  = a0.x  * x0.x  + h.x  * x0.y;
    g.yz = a0.yz * x12.xz + h.yz * x12.yw;
    return 130.0 * dot(m, g);
}
vec4 blendOver(vec4 bg, vec4 fg) {
    float outA = fg.a + bg.a * (1.0 - fg.a);
    if (outA <= 0.0) {
        return vec4(0.0);
    }

    vec3 outRGB = (fg.rgb * fg.a + bg.rgb * bg.a * (1.0 - fg.a)) / outA;
    return vec4(outRGB, outA);
}
float drawCircle(vec2 uv,float n,float m,float ox,float oy,float alpha,float ot){
    float x = uv.x;
    float y = uv.y;
    float d = abs(sqrt((x-ox)*(x-ox)+(y-oy)*(y-oy)*0.2) - mod(GameTime*300.0+ot ,2.0));
    return min(max(alpha-(d-m)/(n-m),0.0),alpha);
}
void main() {
    vec3 color = textureProj(Sampler0, texProj0).rgb * BackgroundColor.rgb;
    for (int i = 0; i < EndPortalLayers; i++) {
        color += textureProj(Sampler1, texProj0 * end_portal_layer(float(i + 1))).rgb * COLORS[i];
    }
    vec2 uv = gl_FragCoord.xy/Resolution;
    vec2 noiseOffset = vec2(mod(GameTime*300.0,1.0),mod(GameTime*200.0,1.0));
    float c1 = drawCircle(uv,0.05,0.005,0.25,0.9,0.5,1.0);
    float c2 = drawCircle(uv,0.05,0.005,0.25,0.1,0.5,0.0);
    float p = max(min((c1+c2),0.5),0)*max((snoise(uv+noiseOffset)),0);
    fragColor = blendOver(vec4(color, BackgroundColor.a),vec4(BackgroundColor.rgb,p));
}
